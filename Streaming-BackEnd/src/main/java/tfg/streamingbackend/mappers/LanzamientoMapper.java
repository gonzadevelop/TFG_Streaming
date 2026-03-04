package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Lanzamiento;
import tfg.streamingbackend.model.CrearCancionDTO;

import java.time.LocalDate;

@Mapper(componentModel = "spring", imports = LocalDate.class)
public interface LanzamientoMapper {

    @Mapping(source = "dto.nombreSencillo", target = "titulo")
    @Mapping(source = "archivoPortada", target = "archivoPortada")
    @Mapping(target = "tipo", constant = "sencillo")
    @Mapping(target = "fechaLanzamiento", expression = "java(LocalDate.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lanzamientoCanciones", ignore = true)
    Lanzamiento toEntity(CrearCancionDTO dto, String archivoPortada);
}

