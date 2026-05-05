package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.CancionNotFoundException;
import tfg.KeySound.exception.album.AlbumNotFoundException;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.CancionMapper;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.mappers.AlbumMapper;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.album.ResponseProximoAlbumDTO;
import tfg.KeySound.model.pista.ResponsePistaDTO;
import tfg.KeySound.model.album.RequestAlbumDTO;
import tfg.KeySound.model.album.ResponseAlbumCompletoDTO;
import tfg.KeySound.repositorys.CancionRepository;
import tfg.KeySound.repositorys.PistaRepository;
import tfg.KeySound.repositorys.AlbumRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;
import tfg.KeySound.utils.ArtistaUtils;
import tfg.KeySound.utils.AudioUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {

    /**
     * Inyecciones por constructor
     */
    private final FirebaseService firebaseService;
    private final JwtService jwtService;
    private final CancionService cancionService;

    private final UsuarioRepository usuarioRepository;
    private final CancionRepository cancionRepository;
    private final AlbumRepository albumRepository;
    private final PistaRepository pistaRepository;

    private final CancionMapper cancionMapper;
    private final AlbumMapper albumMapper;
    private final PistaMapper pistaMapper;
    private final ArtistaMapper artistaMapper;

    /**
     * Metodos llamados por endpoints
     */
    public void subirAlbum(RequestAlbumDTO dto, MultipartFile portada, List<MultipartFile> archivos, String token) {

        // obtener el artista que sube el álbum a partir del token JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(token)));

        /*
            -------------- PORTADA Y ALBUM --------------
         */
        // Validar el formato de la portada
        ArtistaUtils.validarFormatoImagen(portada.getContentType());

        // Crear nombres únicos para la portada y subirla a Firebase Storage

        String nombreArchivoPortada  = portada != null
                ? firebaseService.subirArchivo(portada, artista.getUsername() + "_" + dto.getNombreAlbum() + "_portada")
                : "";

        String tipo = dto.getCanciones().size() == 1
                ? "Sencillo"
                : "Album";

        LocalDateTime fechaLanzamiento = dto.getFechaLanzamiento() != null ?
                dto.getFechaLanzamiento() :
                null;

        // Crear la entidad album sin canciones (se añaden más tarde)
        Album album = albumMapper.createAlbum(dto.getNombreAlbum(), nombreArchivoPortada, tipo, artista, fechaLanzamiento);

         /*
            -------------- CANCIONES --------------
         */

        // Mapear cada canción del DTO a entidad, subiendo el archivo de audio a Firebase Storage y obteniendo colaboradores y duración
        Iterator<MultipartFile> archivoIt = archivos.iterator();

        List<Cancion> canciones = dto.getCanciones().stream()
                .map(rc -> {
                    // Si la canción ya existe, la recuperamos y no consumimos archivo de la lista
                    if (rc.getIdCancionExistente() != null) {
                        return cancionRepository.findById(rc.getIdCancionExistente())
                                .orElseThrow(() -> new CancionNotFoundException(rc.getIdCancionExistente()));
                    }

                    // Si es nueva, extraemos el siguiente binario de la lista recibida
                    MultipartFile archivoActual = archivoIt.next();

                    // Validaciones y obtención de metadatos del binario
                    ArtistaUtils.validarFormatoArchivo(archivoActual.getContentType());
                    Integer duracionSegundos = AudioUtils.obtenerDuracionSegundos(archivoActual);
                    List<Usuario> colaboradores = ArtistaUtils.obtenerColaboradores(rc.getIdArtistas(), artista, usuarioRepository);

                    // Subida a Firebase y mapeo a entidad
                    String urlAudio = firebaseService.subirArchivo(archivoActual, artista.getUsername() + "_" + rc.getTitulo());
                    return cancionMapper.fromData(rc.getTitulo(), urlAudio, colaboradores, duracionSegundos);
                })
                .toList();

        // Guardar primero en la bd el álbum para generar su ID y luego las canciones y la relación
        albumRepository.saveAndFlush(album);
        cancionRepository.saveAllAndFlush(canciones);

        // Crear las relaciones de pista de forma funcional, preservando el orden y el número de pista (1-based)
        List<Pista> pistas = IntStream.range(0, canciones.size())
                .mapToObj(i -> pistaMapper.toEntity(canciones.get(i), album, i + 1))
                .collect(Collectors.toList());

        pistaRepository.saveAll(pistas);
    }

    public ResponseAlbumCompletoDTO visualizarAlbum(Long albumId) {
        // Buscar el album por su ID
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(albumId));

        if (album.getEsBorrador() || album.getFechaLanzamiento().isAfter(LocalDateTime.now())) throw new RuntimeException("El album no está disponible para su visualización");

        // Obtener la URL de la portada del album desde Firebase Storage o usar una imagen por defecto si no tiene portada
        String urlPortada = firebaseService.obtenerUrlArchivoImagen(album.getArchivoPortada(), album.getTitulo());

        // Mapear el album a un DTO y obtener la URL de la portada ordenados por el número de pista
        List <ResponsePistaDTO> cancionesDto = album
                .getPistas()
                .stream()
                .map(p -> {
                    String urlCancion = firebaseService.obtenerUrlArchivoAudio(p.getCancion().getArchivoCancion());

                    List<String> artistas = cancionService.obtenerArtistasDeCancion(p.getCancion().getId())
                            .stream()
                            .map(Usuario::getUsername)
                            .toList();

                    return cancionMapper.toAlbumDto(p, urlCancion, artistas);
                })
                .sorted(Comparator.comparingInt(ResponsePistaDTO::getNumeroPista))
                .toList();

        return albumMapper.toResponseDto(album, cancionesDto, urlPortada);
    }

    public void publicarAlbum(Long albumId, String token) {
        // Buscar el album por su ID
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(albumId));

        // Verificar que el usuario autenticado es el artista del álbum
        String username = jwtService.extractUsername(token);
        if (!album.getUsuario().getUsername().equalsIgnoreCase(username)) throw new RuntimeException("No tienes permiso para publicar este álbum");

        // Verificar que el álbum tiene al menos una canción
        if (album.getPistas() == null || album.getPistas().isEmpty()) throw new RuntimeException("El álbum debe tener al menos una canción para ser publicado");

        // Publicar el álbum estableciendo esBorrador a falso y guardarlo en la base de datos
        album.setEsBorrador(false);
        albumRepository.save(album);
    }

    public List<ResponseProximoAlbumDTO> obtenerProximosLanzamientos() {
        // Obtener los próximos lanzamientos ordenados por fecha de lanzamiento (solo álbumes que no son borradores y con fecha de lanzamiento en el futuro)
        List<Album> proximosLanzamientos = albumRepository.findByEsBorradorFalseAndFechaLanzamientoAfterOrderByFechaLanzamientoAsc(LocalDateTime.now());

        return albumMapper.toProximosAlbumsDtos(proximosLanzamientos);
    }

    public List<ResponseAlbumDTO> obtenerNovedades() {
        // Obtener los álbumes más recientes ordenados por fecha de lanzamiento (solo álbumes que no son borradores y con fecha de lanzamiento en el pasado o presente)
        // Y que su fecha de lanzamiento sea posterior a hace 7 días, es decir, que se hayan lanzado en la última semana
        List<Album> novedades = albumRepository.findByEsBorradorFalseAndFechaLanzamientoAfterOrderByFechaLanzamientoAsc(LocalDateTime.now().minusDays(7));

        return albumMapper.toDtos(novedades);
    }

    public List<ResponseAlbumDTO> buscarAlbums(String q) {
        if (q == null || q.isBlank()) return List.of();

        List<Album> albums = albumRepository.buscarPorTitulo(q);
        List<ResponseAlbumDTO> dtos = albumMapper.toDtos(albums);
        dtos.forEach(dto ->
            dto.setUrlPortada(firebaseService.obtenerUrlArchivoImagen(dto.getUrlPortada(), dto.getTitulo()))
        );
        return dtos;
    }
}
