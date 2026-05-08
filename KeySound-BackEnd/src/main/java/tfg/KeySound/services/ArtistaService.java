package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.CancionMapper;
import tfg.KeySound.mappers.AlbumMapper;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.model.artista.ResponseArtistaHomeDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.album.ResponseMiAlbumDTO;
import tfg.KeySound.model.artista.ResponseArtistaDTO;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.repositorys.AlbumRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;
    private final FirebaseService firebaseService;

    private final UsuarioRepository usuarioRepository;
    private final AlbumRepository albumRepository;

    private final AlbumMapper albumMapper;
    private final ArtistaMapper artistaMapper;
    private final PistaMapper pistaMapper;

    /**
     * Metodos llamados por endpoints
     */
    public ResponseArtistaDTO obtenerInfoArtista(String username, String token) {
        // Buscar el artista por username
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));


        // Buscar el usuario que hace la peticion en la bd (si viene el header) para contar cuántas canciones del artista tiene en favoritos
        int cancionesEnFavoritos = 0;
        boolean sigueAlArtista = false;
        if (!token.isEmpty()) {
            String usernameToken = jwtService.extractUsername(token);
            Usuario usuarioToken = usuarioRepository.findByUsernameIgnoreCase(usernameToken)
                    .orElseThrow(() -> new UsernameNotFoundException(usernameToken));

            cancionesEnFavoritos = usuarioRepository.countFavoritosByUsuarioAndArtista(usuarioToken.getId(), artista.getId());
            sigueAlArtista = usuarioToken.getSeguidos() != null && usuarioToken.getSeguidos().contains(artista);
        }

        // Sacar todos los albums del artista.
        List<Album> albums = artista
                .getAlbums()
                .stream()
                .filter(a -> !a.getEsBorrador()
                        && a.getFechaLanzamiento().isBefore(LocalDateTime.now())) // Filtrar los albums que no son borradores y con fecha de lanzamiento anterior a hoy
                .sorted(Comparator.comparing(Album::getFechaLanzamiento).reversed())
                .toList();

        // Buscar las 10 canciones más populares del artista, ordenadas por número de reproducciones (historialReproducciones)
        List<ResponsePistaHomeDTO> cancionesPopulares =
                Stream.concat(
                        // obtener todas las canciones del artista.
                        artista
                                .getCanciones()
                                .stream(),
                        albums
                                .stream()
                                .flatMap(a -> a.getPistas().stream())
                                .map(Pista::getCancion)
                )
                .collect(Collectors.toMap(
                        // usar el ID de la canción como clave para evitar duplicados (si una canción está en un album y también suelta, por ejemplo)
                        Cancion::getId,
                        c -> c,
                        (existente, reemplazo) -> existente
                ))
                .values()
                .stream()
                .sorted((c1, c2) -> Integer.compare(
                        // comparar por número de reproducciones (historialReproducciones), teniendo en cuenta que puede ser null
                        c2.getHistorialReproducciones() != null ? c2.getHistorialReproducciones().size() : 0,
                        c1.getHistorialReproducciones() != null ? c1.getHistorialReproducciones().size() : 0))
                .limit(10)
                .map( c -> {
                    // Mapear a DTO y añadir la URL de la canción, la portada del album y los artistas (el artista principal del
                    ResponsePistaHomeDTO dto = pistaMapper.toDto(c);
                    dto.setIdPista(c.getPistas().stream().findFirst().get().getId());
                    Album album = c.getPistas().stream().findFirst().get().getAlbum();
                    dto.setIdAlbum(album.getId());
                    dto.setUrlPortada(firebaseService.obtenerUrlArchivoImagen(album.getArchivoPortada(), album.getTitulo()));
                    dto.setUrlCancion(firebaseService.obtenerUrlArchivoAudio(c.getArchivoCancion()));
                    List<String> artistas = Stream.concat(
                                    Stream.of(c.getPistas().stream().findFirst().get().getAlbum().getUsuario()),
                                    c.getUsuarios().stream()
                            )
                            .map(Usuario::getUsername)
                            .toList();
                    dto.setArtistas(artistas);
                    dto.setReproducciones(c.getHistorialReproducciones().size());
                    return dto;
                })
                .toList();

        // Obtener la URL del avatar del artista desde Firebase o de ui-avatars si no tiene avatar
        String urlAvatar = firebaseService.obtenerUrlArchivoImagen(artista.getArchivoAvatar(), artista.getUsername());

        // Mapear a DTO
        List<ResponseAlbumDTO> albumsDTO = albumMapper.toDtos(albums);
        albumsDTO.forEach(albumDTO ->
            albumDTO.setUrlPortada(firebaseService.obtenerUrlArchivoImagen(albumDTO.getUrlPortada(), albumDTO.getTitulo()))
        );

        return artistaMapper.toDto(artista, cancionesPopulares, albumsDTO, cancionesEnFavoritos, urlAvatar, sigueAlArtista);
    }

    public List<ResponseMiAlbumDTO> obtenerMisAlbums(String substring) {
        // obtener el artista a partir del token JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(substring))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(substring)));

        // sacar todos los albums del artista, ordenados por fecha de creación (los más recientes primero)
        List<Album> albums = artista
                .getAlbums()
                .stream()
                .sorted(Comparator.comparing(Album::getFechaLanzamiento).reversed())
                .toList();

        // mapear a DTO
        List<ResponseMiAlbumDTO> albumsDTO = albumMapper.toMisAlbumsDtos(albums);

        // obtener la URL de la portada de cada album desde Firebase o de ui-avatars si no tiene portada
        for (int i = 0; i < albums.size(); i++) {
            String urlPortada = firebaseService.obtenerUrlArchivoImagen(albums.get(i).getArchivoPortada(), albums.get(i).getTitulo());
            albumsDTO.get(i).setPortada(urlPortada);
        }

        return albumsDTO;
    }

    public void publicarAlbum(Long idAlbum, String token) {
        // obtener el artista a partir del JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(token)));

        // buscar el album por ID y comprobar que pertenece al artista
        Album album = artista.getAlbums()
                .stream()
                .filter(a -> a.getId().equals(idAlbum))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Album no encontrado o no pertenece al artista"));

        // comprobar que el album tiene fecha de lanzamiento y portada
        if (album.getFechaLanzamiento() == null) throw new RuntimeException("El album debe tener fecha de lanzamiento y portada para ser publicado");

        // comprobar que la fecha de album no es anterior a hoy
        if (album.getFechaLanzamiento().isBefore(LocalDateTime.now()) || album.getFechaLanzamiento().equals(LocalDateTime.now()))
            throw new RuntimeException("La fecha de album debe ser posterior a hoy para ser publicado");

        // publicar el album
        album.setEsBorrador(false);
        albumRepository.save(album);
    }

    public List<ResponseArtistaHomeDTO> obtenerArtistasQueSigo(String token) {
        // si el token es vacío, devolver una lista vacía
        if (token.isEmpty()) return List.of();

        // obtener el usuario a partir del JWT
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(token)));

        // sacar los artistas que sigue el usuario
        List<Usuario> artistasQueSigo = usuario.getSeguidos().stream()
                .filter(u -> "ROLE_ARTISTA".equals(u.getRol().getNombre()))
                .toList();

        // mapear a DTO
        List<ResponseArtistaHomeDTO> artistasDTO = artistasQueSigo
                .stream()
                .map(artistaMapper::toHomeDto)
                .toList();

        // obtener la URL del avatar de cada artista desde Firebase o de ui-avatars si no tiene avatar
        for (int i = 0; i < artistasQueSigo.size(); i++) {
            String urlAvatar = firebaseService.obtenerUrlArchivoImagen(artistasQueSigo.get(i).getArchivoAvatar(), artistasQueSigo.get(i).getUsername());
            artistasDTO.get(i).setUrlAvatar(urlAvatar);
        }

        return artistasDTO;
    }

    public List<ResponseArtistaHomeDTO> buscarArtistas(String q) {
        if (q == null || q.isBlank()) return List.of();

        List<Usuario> artistas = usuarioRepository.buscarPorUsername(q);
        List<ResponseArtistaHomeDTO> dtos = artistas.stream()
                .map(artistaMapper::toHomeDto)
                .toList();

        for (int i = 0; i < artistas.size(); i++) {
            dtos.get(i).setUrlAvatar(firebaseService.obtenerUrlArchivoImagen(
                    artistas.get(i).getArchivoAvatar(), artistas.get(i).getUsername()));
        }
        return dtos;
    }
}
