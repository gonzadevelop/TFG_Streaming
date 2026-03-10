package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.ArtistaDTO;
import tfg.KeySound.model.CancionDTO;
import tfg.KeySound.model.LanzamientoDTO;

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
