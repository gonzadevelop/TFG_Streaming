package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.CancionMapper;
import tfg.KeySound.mappers.AlbumMapper;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.album.ResponseMiAlbumDTO;
import tfg.KeySound.model.artista.ResponseArtistaDTO;
import tfg.KeySound.repositorys.AlbumRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

import java.time.LocalDateTime;
import java.util.*;

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

    private final CancionMapper cancionMapper;
    private final AlbumMapper albumMapper;
    private final ArtistaMapper artistaMapper;

    /**
     * Metodos llamados por endpoints
     */
    public ResponseArtistaDTO obtenerInfoArtista(String username, String token) {
        // Buscar el artista por username
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));


        // Buscar el usuario que hace la peticion en la bd (si viene el header) para contar cuántas canciones del artista tiene en favoritos
        int cancionesEnFavoritos = 0;
        if (!token.isEmpty()) {
            String usernameToken = jwtService.extractUsername(token);
            Usuario usuarioToken = usuarioRepository.findByUsernameIgnoreCase(usernameToken)
                    .orElseThrow(() -> new UsernameNotFoundException(usernameToken));

            cancionesEnFavoritos = usuarioToken
                    .getFavoritos()
                    .stream()
                    .filter(pista -> pista.getCancion().getUsuarios().contains(artista))
                    .mapToInt(pista -> 1)
                    .sum();
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
        List<Pista> cancionesPopulares = artista.getCanciones()
                .stream()
                .sorted((c1, c2) -> Integer.compare(
                        c2.getHistorialReproducciones() != null ? c2.getHistorialReproducciones().size() : 0,
                        c1.getHistorialReproducciones() != null ? c1.getHistorialReproducciones().size() : 0))
                .limit(10)
                .map(cancion -> cancion.getPistas().stream().findFirst())
                .flatMap(Optional::stream)
                .toList();

        // Obtener la URL del avatar del artista desde Firebase o de ui-avatars si no tiene avatar
        String urlAvatar = firebaseService.obtenerUrlArchivoImagen(artista.getArchivoAvatar(), artista.getUsername());

        // Mapear a DTO
        List<ResponseAlbumDTO> albumsDTO = albumMapper.toDtos(albums);
        List<ResponseCancionArtistaDTO> cancionesPopularesDTO = cancionMapper.toDtos(cancionesPopulares);
        return artistaMapper.toDto(artista, cancionesPopularesDTO, albumsDTO, cancionesEnFavoritos, urlAvatar);
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
}
