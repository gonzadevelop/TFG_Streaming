package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.model.RegisterRequestDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "rol", ignore = true)
    Usuario toEntity(RegisterRequestDTO registerRequestDTO);
}
