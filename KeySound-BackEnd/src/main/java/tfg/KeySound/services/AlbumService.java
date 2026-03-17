package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.CancionNotFoundException;
import tfg.KeySound.exception.album.AlbumNotFoundException;
import tfg.KeySound.mappers.CancionMapper;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.mappers.AlbumMapper;
import tfg.KeySound.model.pista.ResponsePistaDTO;
import tfg.KeySound.model.album.RequestAlbumDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;
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

@Service
@RequiredArgsConstructor
public class AlbumService {

    /**
     * Inyecciones por constructor
     */
    private final FirebaseService firebaseService;
    private final JwtService jwtService;

    private final UsuarioRepository usuarioRepository;
    private final CancionRepository cancionRepository;
    private final AlbumRepository albumRepository;
    private final PistaRepository pistaRepository;

    private final CancionMapper cancionMapper;
    private final AlbumMapper albumMapper;
    private final PistaMapper pistaMapper;

    /**
     * Metodos llamados por endpoints
     */
    public void subirAlbum(RequestAlbumDTO dto, String token) {

        // obtener el artista que sube el álbum a partir del token JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(token)));

        /*
            -------------- PORTADA Y ALBUM --------------
         */
        // Validar el formato de la portada
        ArtistaUtils.validarFormatoImagen(dto.getPortada().getContentType());

        // Crear nombres únicos para la portada y subirla a Firebase Storage

        String nombreArchivoPortada  = dto.getPortada() != null
                ? firebaseService.subirArchivo(dto.getPortada(), artista.getUsername() + "_" + dto.getNombreAlbum() + "_portada")
                : "";

        String tipo = dto.getCanciones().size() == 1 ?
                "Sencillo" :
                "Album";

        LocalDateTime fechaLanzamiento = dto.getFechaLanzamiento() != null ?
                dto.getFechaLanzamiento() :
                null;

        // Crear la entidad album sin canciones (se añaden más tarde)
        Album album = albumMapper.createAlbum(dto.getNombreAlbum(), nombreArchivoPortada, tipo, artista, fechaLanzamiento);

         /*
            -------------- CANCIONES --------------
         */

        // Mapear cada canción del DTO a entidad, subiendo el archivo de audio a Firebase Storage y obteniendo colaboradores y duración
        List<Cancion> canciones = dto.getCanciones()
                .stream()
                .peek(rc -> ArtistaUtils.validarFormatoArchivo(rc.getArchivo().getContentType()))
                .map(rc -> {
                    // Si se ha proporcionado un ID de canción existente, devolverla
                    if (rc.getIdCancionExistente() != null) {
                        return cancionRepository.findById(rc.getIdCancionExistente())
                                .orElseThrow(() -> new CancionNotFoundException(rc.getIdCancionExistente()));
                    }

                    // comprobar cuanto dura el archivo de audio (en segundos)
                    Integer duracionSegundos = AudioUtils.obtenerDuracionSegundos(rc.getArchivo());

                    // obtener colaboradores
                    List<Usuario> colaboradores = ArtistaUtils.obtenerColaboradores(rc.getIdArtistas(), artista, usuarioRepository);

                    // Crear un nombre único para el archivo de audio y subirlo a Firebase
                    String nombreArchivoAudio = firebaseService.subirArchivo(rc.getArchivo(), artista.getUsername() + "_" + rc.getTitulo());

                    // Mapear a entidad y devolverla
                    return cancionMapper.fromData(rc.getTitulo(), nombreArchivoAudio, colaboradores, duracionSegundos);
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

    public ResponseAlbumDTO visualizarAlbum(Long albumId) {
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
                    return cancionMapper.toAlbumDto(p, urlCancion);
                })
                .sorted(Comparator.comparingInt(ResponsePistaDTO::getNumeroPista))
                .toList();

        return albumMapper.toResponseDto(album, cancionesDto, urlPortada);
    }
}
