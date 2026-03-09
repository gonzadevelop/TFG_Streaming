package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.entitys.LanzamientoCancion;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.model.CancionDTO;
import tfg.streamingbackend.model.CrearCancionDTO;

import java.util.List;
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

    @Mapping(target = "idLanzamiento", source = "id")
    @Mapping(target = "titulo", source = "cancion.titulo")
    @Mapping(target = "urlPortada", source = "lanzamiento.archivoPortada")
    @Mapping(target = "artistas", expression = "java(lanzamientoCancion.getCancion().getUsuarios().stream().map(u -> u.getUsername()).toList())")
    @Mapping(target = "urlCancion", source = "cancion.archivoCancion")
    @Mapping(target = "reproducciones", expression = "java((long) (lanzamientoCancion.getCancion().getHistorialReproducciones() != null ? lanzamientoCancion.getCancion().getHistorialReproducciones().size() : 0))")
    CancionDTO toDto(LanzamientoCancion lanzamientoCancion);

    List<CancionDTO> toDtos(List<LanzamientoCancion> lanzamientoCanciones);
}
