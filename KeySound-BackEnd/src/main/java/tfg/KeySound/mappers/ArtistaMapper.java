package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.usuario.ResponseArtistaDTO;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoArtistaDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistaMapper {
    @Mapping(target = "username", source = "artista.username")
    @Mapping(target = "canciones", source = "canciones")
    @Mapping(target = "lanzamientos", source = "lanzamientos")
    @Mapping(target = "seguidores", expression = "java(artista.getSeguidores() != null ? artista.getSeguidores().size() : 0)")
    @Mapping(target = "cancionesEnFavoritos", source = "cancionesEnFavoritos")
    ResponseArtistaDTO toDto(Usuario artista, List<ResponseCancionArtistaDTO> canciones, List<ResponseLanzamientoArtistaDTO> lanzamientos, int cancionesEnFavoritos);
}
