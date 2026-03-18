package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.artista.ResponseArtistaDTO;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistaMapper {
    @Mapping(target = "username", source = "artista.username")
    @Mapping(target = "canciones", source = "canciones")
    @Mapping(target = "albums", source = "albums")
    @Mapping(target = "seguidores", expression = "java(artista.getSeguidores() != null ? artista.getSeguidores().size() : 0)")
    @Mapping(target = "cancionesEnFavoritos", source = "cancionesEnFavoritos")
    @Mapping(target = "urlAvatar", source = "urlAvatar")
    ResponseArtistaDTO toDto(Usuario artista, List<ResponseCancionArtistaDTO> canciones, List<ResponseAlbumDTO> albums, int cancionesEnFavoritos, String urlAvatar);
}
