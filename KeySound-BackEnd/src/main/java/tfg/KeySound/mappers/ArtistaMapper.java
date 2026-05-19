package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.artista.ResponseArtistaDTO;
import tfg.KeySound.model.artista.ResponseArtistaHomeDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.services.external.FirebaseService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ArtistaMapper {

    @Autowired
    protected FirebaseService firebaseService;
    
    @Mapping(target = "username", source = "artista.username")
    @Mapping(target = "canciones", source = "canciones")
    @Mapping(target = "albums", source = "albums")
    @Mapping(target = "seguidores", expression = "java(artista.getSeguidores() != null ? artista.getSeguidores().size() : 0)")
    @Mapping(target = "cancionesEnFavoritos", source = "cancionesEnFavoritos")
    @Mapping(target = "urlAvatar", ignore = true)
    protected abstract ResponseArtistaDTO toDtoBase(Usuario artista, List<ResponsePistaHomeDTO> canciones, List<ResponseAlbumDTO> albums, int cancionesEnFavoritos);

    public ResponseArtistaDTO toDto(Usuario artista, List<ResponsePistaHomeDTO> canciones, List<ResponseAlbumDTO> albums, int cancionesEnFavoritos, boolean sigueAlArtista) {
        ResponseArtistaDTO dto = toDtoBase(artista, canciones, albums, cancionesEnFavoritos);
        // Obtener URL del avatar desde Firebase
        String urlAvatar = firebaseService.obtenerUrlArchivoImagen(artista.getArchivoAvatar(), artista.getUsername());
        dto.setUrlAvatar(urlAvatar);
        dto.setSigueAlArtista(sigueAlArtista);
        return dto;
    }

    @Mapping(target = "id", source = "artista.id")
    @Mapping(target = "username", source = "artista.username")
    @Mapping(target = "urlAvatar", ignore = true)
    protected abstract ResponseArtistaHomeDTO toHomeDtoBase(Usuario artista);

    public ResponseArtistaHomeDTO toHomeDto(Usuario artista) {
        ResponseArtistaHomeDTO dto = toHomeDtoBase(artista);
        // Obtener URL del avatar desde Firebase
        String urlAvatar = firebaseService.obtenerUrlArchivoImagen(artista.getArchivoAvatar(), artista.getUsername());
        dto.setUrlAvatar(urlAvatar);
        return dto;
    }

    public List<ResponseArtistaHomeDTO> toHomeDtos(List<Usuario> artistas) {
        return artistas.stream()
                .map(this::toHomeDto)
                .toList();
    }
}
