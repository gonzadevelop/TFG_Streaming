package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PistaMapper {

    @Mapping(source = "cancion", target = "cancion")
    @Mapping(source = "album", target = "album")
    @Mapping(source = "numeroPista", target = "numeroPista")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlistPistas", ignore = true)
    Pista toEntity(Cancion cancion, Album album, Integer numeroPista);

    @Mapping(target = "idPista", ignore = true)
    @Mapping(target = "idAlbum", ignore = true)
    @Mapping(target = "titulo", source = "cancion.titulo")
    @Mapping(target = "urlPortada", ignore = true)
    @Mapping(target = "urlCancion", ignore = true)
    @Mapping(target = "artistas", ignore = true)
    @Mapping(target = "duracionSegundos", source = "cancion.duracionSegundos")
    ResponsePistaHomeDTO toDto(Cancion cancion);

    List<ResponsePistaHomeDTO> toDtos(List<Cancion> canciones);

    @Mapping(target = "idPista", source = "pista.id")
    @Mapping(target = "titulo", source = "pista.cancion.titulo")
    @Mapping(target = "urlPortada", source = "urlPortada")
    @Mapping(target = "urlCancion", source = "urlCancion")
    @Mapping(target = "artistas", source = "artistas")
    @Mapping(target = "reproducciones", ignore = true)
    @Mapping(target = "duracionSegundos", source = "pista.cancion.duracionSegundos")
    ResponsePistaPlaylistDTO pistaToPlaylistDto(Pista pista, String urlPortada, String urlCancion, List<String> artistas);
}
