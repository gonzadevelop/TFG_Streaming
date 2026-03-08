package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.model.RegisterRequestDTO;
import tfg.streamingbackend.model.UsuarioDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "rol", ignore = true)
    Usuario toEntity(RegisterRequestDTO registerRequestDTO);

    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "email", source = "usuario.email")
    @Mapping(target = "urlAvatar", source = "urlAvatar")
    UsuarioDTO toDto(Usuario usuario, String urlAvatar);
}
