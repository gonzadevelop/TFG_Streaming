package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.model.ArtistaDTO;
import tfg.streamingbackend.model.CancionDTO;
import tfg.streamingbackend.model.LanzamientoDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistaMapper {
    @Mapping(target = "username", source = "artista.username")
    @Mapping(target = "canciones", source = "canciones")
    @Mapping(target = "lanzamientos", source = "lanzamientos")
    @Mapping(target = "seguidores", expression = "java(artista.getSeguidores() != null ? artista.getSeguidores().size() : 0)")
    @Mapping(target = "cancionesEnFavoritos", source = "cancionesEnFavoritos")
    ArtistaDTO toDto(Usuario artista, List<CancionDTO> canciones, List<LanzamientoDTO> lanzamientos, int cancionesEnFavoritos);
}
