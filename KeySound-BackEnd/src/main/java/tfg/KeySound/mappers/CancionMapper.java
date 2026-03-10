package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.LanzamientoCancion;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.CancionDTO;
import tfg.KeySound.model.CrearSencilloDTO;

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
    Cancion toEntity(CrearSencilloDTO dto, String archivoCancion, Set<Usuario> usuarios);

    @Mapping(target = "idLanzamiento", source = "id")
    @Mapping(target = "titulo", source = "cancion.titulo")
    @Mapping(target = "urlPortada", source = "lanzamiento.archivoPortada")
    @Mapping(target = "artistas", expression = "java(lanzamientoCancion.getCancion().getUsuarios().stream().map(u -> u.getUsername()).toList())")
    @Mapping(target = "urlCancion", source = "cancion.archivoCancion")
    @Mapping(target = "reproducciones", expression = "java((long) (lanzamientoCancion.getCancion().getHistorialReproducciones() != null ? lanzamientoCancion.getCancion().getHistorialReproducciones().size() : 0))")
    CancionDTO toDto(LanzamientoCancion lanzamientoCancion);

    List<CancionDTO> toDtos(List<LanzamientoCancion> lanzamientoCanciones);

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoCancion", source = "archivo")
    @Mapping(target = "usuarios", source = "usuarios")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cancionProductores", ignore = true)
    @Mapping(target = "lanzamientoCanciones", ignore = true)
    @Mapping(target = "historialReproducciones", ignore = true)
    @Mapping(target = "duracionSegundos", source = "duracionSegundos")
    Cancion fromData(String titulo, String archivo, List<Usuario> usuarios, Integer duracionSegundos);
}
