package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Rol;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.auth.RegisterRequestDTO;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "username", source = "registerRequestDTO.username")
    @Mapping(target = "email", source = "registerRequestDTO.email")
    @Mapping(target = "password", source = "registerRequestDTO.password")
    @Mapping(target = "rol", source = "rol")
    Usuario toEntity(RegisterRequestDTO registerRequestDTO, Rol rol);

    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "email", source = "usuario.email")
    @Mapping(target = "urlAvatar", source = "urlAvatar")
    ResponseUsuarioDTO toDto(Usuario usuario, String urlAvatar);
}
