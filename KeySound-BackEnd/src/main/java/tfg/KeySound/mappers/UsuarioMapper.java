package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Rol;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.auth.RegisterRequestDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "username", source = "registerRequestDTO.username")
    @Mapping(target = "email", source = "registerRequestDTO.email")
    @Mapping(target = "password", source = "registerRequestDTO.password")
    @Mapping(target = "rol", source = "rol")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "biografia", expression = "java(\"\")")
    Usuario toEntity(RegisterRequestDTO registerRequestDTO, Rol rol);

    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "email", source = "usuario.email")
    @Mapping(target = "biografia", source = "usuario.biografia")
    @Mapping(target = "urlAvatar", source = "urlAvatar")
    @Mapping(target = "playlists", source = "playlists")
    ResponseUsuarioDTO toDto(Usuario usuario, String urlAvatar, List<ResponsePlaylistDTO> playlists);
}
