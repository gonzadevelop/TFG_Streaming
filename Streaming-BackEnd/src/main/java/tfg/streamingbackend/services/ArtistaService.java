package tfg.streamingbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.entitys.Lanzamiento;
import tfg.streamingbackend.entitys.LanzamientoCancion;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.exception.auth.UsernameNotFoundException;
import tfg.streamingbackend.exception.cancion.FileUploadException;
import tfg.streamingbackend.exception.cancion.InvalidFormatFileException;
import tfg.streamingbackend.exception.cancion.OwnershipRequiredException;
import tfg.streamingbackend.exception.lanzamiento.InvalidReleaseTypeException;
import tfg.streamingbackend.exception.lanzamiento.LanzamientoNotFoundException;
import tfg.streamingbackend.exception.lanzamiento.MissingTrackException;
import tfg.streamingbackend.exception.lanzamiento.RelationNotFoundException;
import tfg.streamingbackend.firebase.FirebaseService;
import tfg.streamingbackend.mappers.ArtistaMapper;
import tfg.streamingbackend.mappers.CancionMapper;
import tfg.streamingbackend.mappers.LanzamientoCancionMapper;
import tfg.streamingbackend.mappers.LanzamientoMapper;
import tfg.streamingbackend.model.ArtistaDTO;
import tfg.streamingbackend.model.CancionDTO;
import tfg.streamingbackend.model.CrearCancionDTO;
import tfg.streamingbackend.model.LanzamientoDTO;
import tfg.streamingbackend.repositorys.CancionRepository;
import tfg.streamingbackend.repositorys.LanzamientoCancionRepository;
import tfg.streamingbackend.repositorys.LanzamientoRepository;
import tfg.streamingbackend.repositorys.UsuarioRepository;
import tfg.streamingbackend.security.JwtService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    // --------------- INYECCIONES POR CONSTRUCTOR ---------------
    private final FirebaseService firebaseService;
    private final JwtService jwtService;

    private final UsuarioRepository usuarioRepository;
    private final CancionRepository cancionRepository;
    private final LanzamientoRepository lanzamientoRepository;
    private final LanzamientoCancionRepository lanzamientoCancionRepository;

    private final CancionMapper cancionMapper;
    private final LanzamientoMapper lanzamientoMapper;
    private final LanzamientoCancionMapper lanzamientoCancionMapper;
    private final ArtistaMapper artistaMapper;

    // -------------- MÉTODOS LLAMADOS POR ENDPOINTS --------------

    public void subirSencillo(CrearCancionDTO dto, String token) {

        /*
              ------------ VALIDACIONES INICIALES -------------
         */

        // Validar el formato del archivo de audio
        validarFormatoArchivo(dto.getCancion().getContentType());

        // Validar el formato de la portada
        validarFormatoImagen(dto.getPortada().getContentType());

        // Extraer el nombre del artista del JWT
        String nombreArtista = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(nombreArtista)
                .orElseThrow(() -> new UsernameNotFoundException(nombreArtista));

        /*
                ------------ SUBIDA DE ARCHIVOS A FIREBASE STORAGE ------
         */

        // Crear nombres únicos para los archivos
        String uuid = UUID.randomUUID().toString();
        String nombreArchivoAudio = nombreArtista + "_" + dto.getNombreSencillo() + "_" + uuid;
        String nombreArchivoPortada = nombreArtista + "_" + dto.getNombreSencillo() + "_portada_" + uuid;

        // Subir los archivos a Firebase Storage
        try {
            firebaseService.subirArchivo(dto.getCancion(), nombreArchivoAudio);
            firebaseService.subirArchivo(dto.getPortada(), nombreArchivoPortada);
        } catch (IOException e) {
            throw new FileUploadException();
        }

        /*
                ------------ CREACIÓN DE ENTIDADES Y RELACIONES -------------
         */

        // Obtener los usuarios colaboradores del DTO y añadir el artista que sube
        Set<Usuario> usuarios = new HashSet<>();
        if (dto.getIdUsuarios() != null && !dto.getIdUsuarios().isEmpty()) {
            usuarios.addAll(usuarioRepository.findAllById(dto.getIdUsuarios()));
        }
        usuarios.add(artista);

        // Mapeo de DTO a entidades
        Cancion cancion = cancionMapper.toEntity(dto, nombreArchivoAudio, usuarios);
        Lanzamiento lanzamiento = lanzamientoMapper.toEntity(dto, nombreArchivoPortada);

        // Guardar primero cancion y lanzamiento
        cancionRepository.saveAndFlush(cancion);
        lanzamientoRepository.saveAndFlush(lanzamiento);

        // Crear y guardar la relación
        LanzamientoCancion lanzamientoCancion = lanzamientoCancionMapper.toEntity(cancion, lanzamiento, 1);
        lanzamientoCancionRepository.save(lanzamientoCancion);



    }

    public void eliminarSencillo(Long idSencillo, String token) {
        /*
         ------------- VALIDACIONES INICIALES -------------
         */

        // Buscar el lanzamiento por ID
        Lanzamiento lanzamiento = lanzamientoRepository.findById(idSencillo)
                .orElseThrow(() -> new LanzamientoNotFoundException(idSencillo));

        // Verificar que el lanzamiento es un sencillo
        if (!"sencillo".equalsIgnoreCase(lanzamiento.getTipo())) {
            throw new InvalidReleaseTypeException(idSencillo);
        }

        // Obtener la canción asociada al lanzamiento
        Cancion cancion = lanzamiento.getLanzamientoCanciones().stream().findFirst()
                .orElseThrow(() -> new MissingTrackException(idSencillo))
                .getCancion();

        // Extraer el nombre del artista del JWT
        String nombreArtista = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(nombreArtista)
                .orElseThrow(() -> new UsernameNotFoundException(nombreArtista));

        // Verificar que el artista es el propietario de la canción
        if (!cancion.getUsuarios().contains(artista)) {
            throw new OwnershipRequiredException();
        }

        /*
         ------------- ELIMINACIÓN DE ARCHIVOS EN FIREBASE STORAGE -------------
         */

        // Eliminar el archivo de audio de Firebase Storage
        firebaseService.borrarArchivo(cancion.getArchivoCancion());

        // Eliminar el archivo de portada de Firebase Storage
        firebaseService.borrarArchivo(lanzamiento.getArchivoPortada());

        /*
         ------------- ELIMINACIÓN DE ENTIDADES Y RELACIONES EN LA BASE DE DATOS -------------
         */

        // Eliminar la relación entre lanzamiento y canción
        LanzamientoCancion lanzamientoCancion = lanzamientoCancionRepository.findByLanzamientoIdAndCancionId(idSencillo, cancion.getId())
                .orElseThrow(() -> new RelationNotFoundException());

        lanzamientoCancionRepository.delete(lanzamientoCancion);

        // Eliminar la canción
        cancionRepository.delete(cancion);
        // Eliminar el lanzamiento
        lanzamientoRepository.delete(lanzamiento);


    }

    public ArtistaDTO obtenerInfoArtista(String username, String token) {
        // Buscar el artista por username
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));


        // Buscar el usuario que hace la peticion en la bd (si viene el header) para contar cuántas canciones del artista tiene en favoritos
        int cancionesEnFavoritos = 0;
        if (token != null) {
            String usernameToken = jwtService.extractUsername(token);
            Usuario usuarioToken = usuarioRepository.findByUsernameIgnoreCase(usernameToken)
                    .orElseThrow(() -> new UsernameNotFoundException(usernameToken));

            cancionesEnFavoritos = usuarioToken
                    .getFavoritos()
                    .stream()
                    .filter(lanzamientoCancion -> lanzamientoCancion.getCancion().getUsuarios().contains(artista))
                    .mapToInt(lanzamientoCancion -> 1)
                    .sum();
        }

        // Sacar todos los lanzamientos del artista.
        List<Lanzamiento> lanzamientos = artista.getLanzamientos()
                .stream()
                .toList();

        // Buscar las 10 canciones más populares del artista, ordenadas por número de reproducciones (historialReproducciones)
        List<LanzamientoCancion> cancionesPopulares = artista.getCanciones().stream()
                .sorted((c1, c2) -> Integer.compare(
                        c2.getHistorialReproducciones() != null ? c2.getHistorialReproducciones().size() : 0,
                        c1.getHistorialReproducciones() != null ? c1.getHistorialReproducciones().size() : 0))
                .limit(10)
                .map(cancion -> cancion.getLanzamientoCanciones().stream().findFirst().orElse(null))
                .filter(lc -> lc != null)
                .toList();

        // Mapear a DTO
        List<LanzamientoDTO> lanzamientosDTO = lanzamientoMapper.toDtos(lanzamientos);
        List<CancionDTO> cancionesPopularesDTO = cancionMapper.toDtos(cancionesPopulares);
        return artistaMapper.toDto(artista, cancionesPopularesDTO, lanzamientosDTO, cancionesEnFavoritos);
    }

    // -------------------- MÉTODOS AUXILIARES --------------------

    private void validarFormatoArchivo(String tipo) {
        // Lista de formatos permitidos
        List<String> formatosPermitidos = Arrays.asList(
                "audio/mpeg",
                "audio/wav",
                "audio/x-wav"
        );

        if (tipo == null || !formatosPermitidos.contains(tipo)) {
            throw new InvalidFormatFileException(tipo);
        }
    }

    private void validarFormatoImagen(String tipo) {
        // Lista de formatos de imagen permitidos
        List<String> formatosPermitidos = Arrays.asList(
                "image/jpeg",
                "image/png",
                "image/webp"
        );

        if (tipo == null || !formatosPermitidos.contains(tipo)) {
            throw new InvalidFormatFileException(tipo);
        }
    }


}