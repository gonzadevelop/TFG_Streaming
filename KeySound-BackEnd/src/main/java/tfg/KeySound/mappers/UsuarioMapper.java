package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.RegisterRequestDTO;
import tfg.KeySound.model.UsuarioDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "rol", ignore = true)
    Usuario toEntity(RegisterRequestDTO registerRequestDTO);

    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "email", source = "usuario.email")
    @Mapping(target = "urlAvatar", source = "urlAvatar")
    UsuarioDTO toDto(Usuario usuario, String urlAvatar);
}
