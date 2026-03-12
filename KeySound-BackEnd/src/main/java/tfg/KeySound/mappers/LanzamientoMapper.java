package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Lanzamiento;
import tfg.KeySound.model.lanzamiento.RequestSencilloDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoArtistaDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoDTO;
import tfg.KeySound.model.cancion.ResponseCancionLanzamientoDTO;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDate.class)
public interface LanzamientoMapper {

    @Mapping(source = "dto.nombreSencillo", target = "titulo")
    @Mapping(source = "archivoPortada", target = "archivoPortada")
    @Mapping(target = "tipo", constant = "sencillo")
    @Mapping(target = "fechaLanzamiento", expression = "java(LocalDate.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lanzamientoCanciones", ignore = true)
    Lanzamiento toEntity(RequestSencilloDTO dto, String archivoPortada);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "urlPortada", source = "archivoPortada")
    @Mapping(target = "anioLanzamiento", expression = "java(lanzamiento.getFechaLanzamiento() != null ? lanzamiento.getFechaLanzamiento().getYear() : 0)")
    @Mapping(target = "tipo", source = "tipo")
    ResponseLanzamientoArtistaDTO toDto(Lanzamiento lanzamiento);

    List<ResponseLanzamientoArtistaDTO> toDtos(List<Lanzamiento> lanzamientos);

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoPortada", source = "archivoPortada")
    @Mapping(target = "fechaLanzamiento", expression = "java(LocalDate.now())")
    @Mapping(target = "tipo", constant = "album")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lanzamientoCanciones", ignore = true)
    Lanzamiento createAlbum(String titulo, String archivoPortada);

    @Mapping(target = "nombreLanzamiento", source = "lanzamiento.titulo")
    @Mapping(target = "portada", source = "urlPortada")
    @Mapping(target = "anioLanzamiento", expression = "java(lanzamiento.getFechaLanzamiento() != null ? lanzamiento.getFechaLanzamiento().getYear() : 0)")
    @Mapping(target = "duracionTotalSegundos", expression = "java(canciones == null ? 0 : canciones.stream().mapToInt(c -> c.getDuracionSegundos()).sum())")
    @Mapping(target = "numCanciones", expression = "java(canciones == null ? 0 : canciones.size())")
    @Mapping(target = "tipo", source = "lanzamiento.tipo")
    @Mapping(target = "canciones", source = "canciones")
    ResponseLanzamientoDTO toResponseDto(Lanzamiento lanzamiento, List<ResponseCancionLanzamientoDTO> canciones, String urlPortada);
}
