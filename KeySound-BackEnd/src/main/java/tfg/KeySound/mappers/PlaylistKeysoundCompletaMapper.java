package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.PlaylistKeysound;
import tfg.KeySound.model.pista.ResponsePistaTopPlaylistDTO;
import tfg.KeySound.model.playlist.ResponseKeySoundPlaylistCompletaDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlaylistKeysoundCompletaMapper {

    @Mapping(source = "playlistKeysound.id", target = "id")
    @Mapping(source = "playlistKeysound.nombre", target = "nombre")
    @Mapping(source = "playlistKeysound.descripcion", target = "descripcion")
    @Mapping(source = "urlPortada", target = "urlPortada")
    @Mapping(source = "pistas", target = "pistas")
    @Mapping(target = "esPropia", constant = "false")
    ResponseKeySoundPlaylistCompletaDTO toDto(PlaylistKeysound playlistKeysound,String urlPortada, List<ResponsePistaTopPlaylistDTO> pistas);
}
