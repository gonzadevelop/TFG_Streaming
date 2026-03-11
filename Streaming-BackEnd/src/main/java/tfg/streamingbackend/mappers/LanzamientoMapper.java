package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Lanzamiento;
import tfg.streamingbackend.model.CrearSencilloDTO;
import tfg.streamingbackend.model.LanzamientoDTO;

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
    Lanzamiento toEntity(CrearSencilloDTO dto, String archivoPortada);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "urlPortada", source = "archivoPortada")
    @Mapping(target = "anioLanzamiento", expression = "java(lanzamiento.getFechaLanzamiento() != null ? lanzamiento.getFechaLanzamiento().getYear() : 0)")
    @Mapping(target = "tipo", source = "tipo")
    LanzamientoDTO toDto(tfg.streamingbackend.entitys.Lanzamiento lanzamiento);

    List<LanzamientoDTO> toDtos(List<Lanzamiento> lanzamientos);

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoPortada", source = "archivoPortada")
    @Mapping(target = "fechaLanzamiento", expression = "java(LocalDate.now())")
    @Mapping(target = "tipo", constant = "album")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lanzamientoCanciones", ignore = true)
    Lanzamiento createAlbum(String titulo, String archivoPortada);
}
