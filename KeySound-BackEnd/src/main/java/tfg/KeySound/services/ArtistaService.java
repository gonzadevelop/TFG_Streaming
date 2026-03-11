package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Lanzamiento;
import tfg.KeySound.entitys.LanzamientoCancion;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.OwnershipRequiredException;
import tfg.KeySound.exception.lanzamiento.InvalidReleaseTypeException;
import tfg.KeySound.exception.lanzamiento.LanzamientoNotFoundException;
import tfg.KeySound.exception.lanzamiento.MissingTrackException;
import tfg.KeySound.exception.lanzamiento.RelationNotFoundException;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.CancionMapper;
import tfg.KeySound.mappers.LanzamientoCancionMapper;
import tfg.KeySound.mappers.LanzamientoMapper;
import tfg.KeySound.model.cancion.RequestCancionAlbumDTO;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.lanzamiento.RequestAlbumDTO;
import tfg.KeySound.model.lanzamiento.RequestSencilloDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoArtistaDTO;
import tfg.KeySound.model.usuario.ResponseArtistaDTO;
import tfg.KeySound.repositorys.CancionRepository;
import tfg.KeySound.repositorys.LanzamientoCancionRepository;
import tfg.KeySound.repositorys.LanzamientoRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.JwtService;
import tfg.KeySound.utils.ArtistaUtils;
import tfg.KeySound.utils.AudioUtils;

import java.util.*;

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

    public void subirSencillo(RequestSencilloDTO dto, String token) {

        /*
              ------------ VALIDACIONES INICIALES -------------
         */

        // Validar el formato del archivo de audio
        ArtistaUtils.validarFormatoArchivo(dto.getCancion().getContentType());

        // Validar el formato de la portada
        ArtistaUtils.validarFormatoImagen(dto.getPortada().getContentType());

        // Extraer el nombre del artista del JWT
        String nombreArtista = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(nombreArtista)
                .orElseThrow(() -> new UsernameNotFoundException(nombreArtista));

        /*
                ------------ SUBIDA DE ARCHIVOS A FIREBASE STORAGE ------
         */

        // Crear nombres únicos para los archivos
        String archivoAudio = nombreArtista + "_" + dto.getNombreSencillo();
        String archivoPortada = nombreArtista + "_" + dto.getNombreSencillo() + "_portada";

        // Subir los archivos a Firebase Storage
        String nombreArchivoAudio = firebaseService.subirArchivo(dto.getCancion(), archivoAudio);
        String nombreArchivoPortada = firebaseService.subirArchivo(dto.getPortada(), archivoPortada);

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
        // Save and flush para asegurar que se generan los ID necesarios para la relación
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
                .orElseThrow(RelationNotFoundException::new);

        lanzamientoCancionRepository.delete(lanzamientoCancion);

        // Eliminar la canción
        cancionRepository.delete(cancion);
        // Eliminar el lanzamiento
        lanzamientoRepository.delete(lanzamiento);


    }

    public ResponseArtistaDTO obtenerInfoArtista(String username, String token) {
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
                .filter(Objects::nonNull)
                .toList();

        // Mapear a DTO
        List<ResponseLanzamientoArtistaDTO> lanzamientosDTO = lanzamientoMapper.toDtos(lanzamientos);
        List<ResponseCancionArtistaDTO> cancionesPopularesDTO = cancionMapper.toDtos(cancionesPopulares);
        return artistaMapper.toDto(artista, cancionesPopularesDTO, lanzamientosDTO, cancionesEnFavoritos);
    }

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

        // Crear la entidad album sin canciones (se añaden más tarde)
        Lanzamiento album = lanzamientoMapper.createAlbum(dto.getNombreAlbum(), nombreArchivoPortada);

        /*
            -------------- CANCIONES -------------
         */
        List<Cancion> canciones = new ArrayList<>();

        // Validar los colaboradores de cada canción, crear nombres únicos para los archivos de cada canción, subirlos a Firebase Storage y mapear a entidades Cancion
        for (RequestCancionAlbumDTO cancion : dto.getCanciones()) {
            // Si se ha proporcionado un ID de canción existente, buscarla en la base de datos y añadirla a la lista de canciones
            if (cancion.getIdCancionExistente() != null) {
                Cancion cancionExistente = cancionRepository.findById(cancion.getIdCancionExistente())
                        .orElseThrow(() -> new RuntimeException("ID de canción existente no encontrado: " + cancion.getIdCancionExistente()));

                canciones.add(cancionExistente);

                continue;
            }

            // comprobar cuanto dura el archivo de audio (en segundos) y guardarlo en una variable
            Integer duracionSegundos = 0;
            duracionSegundos = AudioUtils.obtenerDuracionSegundos(cancion.getArchivo());

            // validar formato del archivo
            ArtistaUtils.validarFormatoArchivo(cancion.getArchivo().getContentType());

            List<Usuario> colaboradores = ArtistaUtils.obtenerColaboradores(cancion.getIdArtistas(), artista, usuarioRepository);

            // Crear un nombre único para el archivo de audio de la canción y subirlo a Firebase Storage
            String nombreArchivoAudio = firebaseService.subirArchivo(cancion.getArchivo(), artista.getUsername() + "_" + cancion.getTitulo());

            // Mapear a entidad y asignar duración
            Cancion cancionEntidad = cancionMapper.fromData(cancion.getTitulo(), nombreArchivoAudio, colaboradores, duracionSegundos);
            canciones.add(cancionEntidad);
        }

        /*
            -------------- GUARDAR EN BASE DE DATOS -------------
         */

        // Guardar primero el álbum para generar su ID y luego las canciones y la relación
        lanzamientoRepository.saveAndFlush(album);
        cancionRepository.saveAllAndFlush(canciones);

        List<LanzamientoCancion> lanzamientoCanciones = new ArrayList<>();
        int numeroPista = 1;
        for (Cancion c : canciones) {
            LanzamientoCancion lc = lanzamientoCancionMapper.toEntity(c, album, numeroPista++);
            lanzamientoCanciones.add(lc);
        }

        lanzamientoCancionRepository.saveAll(lanzamientoCanciones);
    }
}
