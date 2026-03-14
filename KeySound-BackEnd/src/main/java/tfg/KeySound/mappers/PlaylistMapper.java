package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Playlist;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {

    @Mapping(source = "dto.nombrePlaylist", target = "nombre")
    @Mapping(source = "dto.esPublica", target = "esPublica")
    @Mapping(source = "usuario", target = "propietario")
    @Mapping(source = "nombreArchivo", target = "fotoPortada", defaultValue = "")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "descripcion", source = "dto.descripcion", defaultValue = "")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "playlistLanzamientoCanciones", ignore = true)
    Playlist toEntity(RequestPlaylistDTO dto, Usuario usuario, String nombreArchivo);

    @Mapping(source = "playlist.id", target = "id")
    @Mapping(source = "playlist.nombre", target = "nombre")
    @Mapping(source = "playlist.descripcion", target = "descripcion")
    @Mapping(source = "url", target = "urlPortada")
    ResponsePlaylistDTO toDto(Playlist playlist, String url);
}

