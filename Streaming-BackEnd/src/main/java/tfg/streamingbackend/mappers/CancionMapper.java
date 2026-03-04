package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.model.CrearCancionDTO;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CancionMapper {

    @Mapping(source = "dto.nombreSencillo", target = "titulo")
    @Mapping(source = "archivoCancion", target = "archivoCancion")
    @Mapping(source = "usuarios", target = "usuarios")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cancionProductores", ignore = true)
    @Mapping(target = "lanzamientoCanciones", ignore = true)
    Cancion toEntity(CrearCancionDTO dto, String archivoCancion, Set<Usuario> usuarios);
}
