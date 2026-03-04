package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Playlist;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.model.CrearPlaylistDTO;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {

    @Mapping(source = "dto.nombrePlaylist", target = "nombre")
    @Mapping(source = "dto.esPublica", target = "esPublica")
    @Mapping(source = "usuario", target = "propietario")
    @Mapping(source = "nombreArchivo", target = "fotoPortada", defaultValue = "TODO: imagen por defecto")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "playlistLanzamientoCanciones", ignore = true)
    Playlist toEntity(CrearPlaylistDTO dto, Usuario usuario, String nombreArchivo);
}

