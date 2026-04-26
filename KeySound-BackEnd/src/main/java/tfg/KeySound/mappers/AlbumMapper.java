package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.album.ResponseAlbumCompletoDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.album.ResponseMiAlbumDTO;
import tfg.KeySound.model.album.ResponseProximoAlbumDTO;
import tfg.KeySound.model.pista.ResponsePistaDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDateTime.class, uses = {ArtistaMapper.class})
public interface AlbumMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "artista", source = "usuario.username")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "urlPortada", source = "archivoPortada")
    @Mapping(target = "anioLanzamiento", source = "album.fechaLanzamiento.year")
    @Mapping(target = "tipo", source = "tipo")
    ResponseAlbumDTO toDto(Album album);

    List<ResponseAlbumDTO> toDtos(List<Album> albums);

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoPortada", source = "archivoPortada")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pistas", ignore = true)
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "esBorrador", constant = "true")
    Album createAlbum(String titulo, String archivoPortada, String tipo, Usuario usuario, LocalDateTime fechaLanzamiento);

    @Mapping(target = "titulo", source = "album.titulo")
    @Mapping(target = "portada", source = "urlPortada")
    @Mapping(target = "anioLanzamiento", expression = "java(album.getFechaLanzamiento() != null ? album.getFechaLanzamiento().getYear() : 0)")
    @Mapping(target = "duracionTotalSegundos", expression = "java(canciones == null ? 0 : canciones.stream().mapToInt(c -> c.getDuracionSegundos()).sum())")
    @Mapping(target = "numCanciones", expression = "java(canciones == null ? 0 : canciones.size())")
    @Mapping(target = "tipo", source = "album.tipo")
    @Mapping(target = "canciones", source = "canciones")
    @Mapping(target = "artista", source = "album.usuario.username")
    ResponseAlbumCompletoDTO toResponseDto(Album album, List<ResponsePistaDTO> canciones, String urlPortada);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "portada", ignore = true)
    @Mapping(target = "esBorrador", source = "esBorrador")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "numeroCanciones", expression = "java(album.getPistas() == null ? 0 : album.getPistas().size())")
    @Mapping(target = "tipo", source = "tipo")
    ResponseMiAlbumDTO toMiAlbumDto(Album album);

    List<ResponseMiAlbumDTO> toMisAlbumsDtos(List<Album> albums);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "artista", source = "usuario.username")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "urlPortada", source = "archivoPortada")
    @Mapping(target = "fechaLanzamiento", source = "album.fechaLanzamiento")
    @Mapping(target = "tipo", source = "tipo")
    ResponseProximoAlbumDTO toProximoAlbumDto(Album album);

    List<ResponseProximoAlbumDTO> toProximosAlbumsDtos(List<Album> albums);
}
