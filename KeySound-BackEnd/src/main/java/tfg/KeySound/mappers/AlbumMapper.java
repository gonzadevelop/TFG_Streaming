package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.album.*;
import tfg.KeySound.model.pista.ResponsePistaDTO;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.utils.ArtistaUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Mapper(
        componentModel = "spring",
        imports = {LocalDateTime.class, ArtistaUtils.class}, // Importamos la utilidad
        uses = {ArtistaMapper.class}
)
public abstract class AlbumMapper {

    @Autowired
    protected FirebaseService firebaseService;

    @Autowired
    protected CancionMapper cancionMapper;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "artista", source = "usuario.username")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "urlPortada", ignore = true)
    @Mapping(target = "anioLanzamiento", source = "album.fechaLanzamiento.year")
    @Mapping(target = "tipo", source = "tipo")
    protected abstract ResponseAlbumDTO toDtoBase(Album album);

    public ResponseAlbumDTO toDto(Album album) {
        ResponseAlbumDTO dto = toDtoBase(album);
        // Obtener URL de portada desde Firebase
        String urlPortada = firebaseService.obtenerUrlArchivoImagen(
                album.getArchivoPortada(),
                album.getTitulo()
        );
        dto.setUrlPortada(urlPortada);
        return dto;
    }

    public List<ResponseAlbumDTO> toDtos(List<Album> albums) {
        return albums.stream()
                .map(this::toDto)
                .toList();
    }

    public Album toEntity(RequestAlbumDTO dto, Usuario artista, MultipartFile portada) {
        if (dto == null) return null;

        // 1. Validar formato si hay portada
        if (portada != null && !portada.isEmpty()) {
            ArtistaUtils.validarFormatoImagen(portada.getContentType());
        }

        // 2. Subir a Firebase (Centralizado aquí)
        String nombreArchivoPortada = (portada != null && !portada.isEmpty())
                ? firebaseService.subirArchivo(portada, artista.getUsername() + "_" + dto.getNombreAlbum() + "_portada")
                : "";

        // 3. Lógica de tipo
        String tipo = (dto.getCanciones() != null && dto.getCanciones().size() == 1)
                ? "Sencillo"
                : "Album";

        // 4. Fecha de lanzamiento
        LocalDateTime fechaLanzamiento = dto.getFechaLanzamiento();

        // 5. Llamada al mapeador automático
        return createAlbum(dto.getNombreAlbum(), nombreArchivoPortada, tipo, artista, fechaLanzamiento);
    }

    // Cambiamos a protected porque ahora el Service usará toEntity()
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoPortada", source = "archivoPortada")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pistas", ignore = true)
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "esBorrador", constant = "true")
    protected abstract Album createAlbum(String titulo, String archivoPortada, String tipo, Usuario usuario, LocalDateTime fechaLanzamiento);

    @Mapping(target = "titulo", source = "album.titulo")
    @Mapping(target = "portada", ignore = true)
    @Mapping(target = "anioLanzamiento", expression = "java(album.getFechaLanzamiento() != null ? album.getFechaLanzamiento().getYear() : 0)")
    @Mapping(target = "duracionTotalSegundos", expression = "java(canciones == null ? 0 : canciones.stream().mapToInt(c -> c.getDuracionSegundos()).sum())")
    @Mapping(target = "numCanciones", expression = "java(canciones == null ? 0 : canciones.size())")
    @Mapping(target = "tipo", source = "album.tipo")
    @Mapping(target = "canciones", source = "canciones")
    @Mapping(target = "artista", source = "album.usuario.username")
    protected abstract ResponseAlbumCompletoDTO toResponseDtoBase(Album album, List<ResponsePistaDTO> canciones);

    public ResponseAlbumCompletoDTO toResponseDto(Album album) {
        String urlPortadaActual = firebaseService.obtenerUrlArchivoImagen(album.getArchivoPortada(), album.getTitulo());

        List<ResponsePistaDTO> cancionesDto = cancionMapper.toAlbumDtos(album
                .getPistas()
                .stream()
                .toList()
        )
                .stream()
                .sorted(Comparator.comparingInt(ResponsePistaDTO::getNumeroPista))
                .toList();

        ResponseAlbumCompletoDTO dto = toResponseDtoBase(album, cancionesDto);
        dto.setPortada(urlPortadaActual);
        return dto;
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "portada", ignore = true)
    @Mapping(target = "esBorrador", source = "esBorrador")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "numeroCanciones", expression = "java(album.getPistas() == null ? 0 : album.getPistas().size())")
    @Mapping(target = "tipo", source = "tipo")
    protected abstract ResponseMiAlbumDTO toMiAlbumDtoBase(Album album);

    public ResponseMiAlbumDTO toMiAlbumDto(Album album) {
        ResponseMiAlbumDTO dto = toMiAlbumDtoBase(album);
        // Obtener URL de portada desde Firebase
        String urlPortada = firebaseService.obtenerUrlArchivoImagen(
                album.getArchivoPortada(),
                album.getTitulo()
        );
        dto.setPortada(urlPortada);
        return dto;
    }

    public List<ResponseMiAlbumDTO> toMisAlbumsDtos(List<Album> albums) {
        return albums.stream()
                .map(this::toMiAlbumDto)
                .toList();
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "artista", source = "usuario.username")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "urlPortada", source = "archivoPortada")
    @Mapping(target = "fechaLanzamiento", source = "album.fechaLanzamiento")
    @Mapping(target = "tipo", source = "tipo")
    public abstract ResponseProximoAlbumDTO toProximoAlbumDto(Album album);

    public abstract List<ResponseProximoAlbumDTO> toProximosAlbumsDtos(List<Album> albums);
}