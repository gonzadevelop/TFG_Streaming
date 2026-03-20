package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.PlaylistKeysound;
import tfg.KeySound.model.playlist.ResponseKeySoundPlaylistDTO;

@Mapper(componentModel = "spring")
public interface PlaylistKeysoundMapper {

    @Mapping(source = "urlPlaylist", target = "urlPlaylist")
    @Mapping(source = "playlistKeysound.nombre", target = "nombre")
    @Mapping(source = "playlistKeysound.descripcion", target = "descripcion")
    @Mapping(source = "urlPortada", target = "urlPortada")
    ResponseKeySoundPlaylistDTO toDto(PlaylistKeysound playlistKeysound, String urlPlaylist, String urlPortada);
}
