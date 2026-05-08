package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import tfg.KeySound.entitys.Rol;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.auth.RegisterRequestDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;
import tfg.KeySound.services.external.FirebaseService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UsuarioMapper {

    @Autowired
    protected FirebaseService firebaseService;

    @Mapping(target = "username", source = "registerRequestDTO.username")
    @Mapping(target = "email", source = "registerRequestDTO.email")
    @Mapping(target = "password", source = "registerRequestDTO.password")
    @Mapping(target = "rol", source = "rol")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "biografia", expression = "java(\"\")")
    public abstract Usuario toEntity(RegisterRequestDTO registerRequestDTO, Rol rol);

    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "email", source = "usuario.email")
    @Mapping(target = "biografia", source = "usuario.biografia")
    @Mapping(target = "urlAvatar", expression = "java(mapearUrlAvatar(usuario))")
    @Mapping(target = "playlists", source = "playlists")
    public abstract ResponseUsuarioDTO toDto(Usuario usuario, List<ResponsePlaylistDTO> playlists);

    /**
     * Obtiene la URL del avatar desde Firebase
     */
    protected String mapearUrlAvatar(Usuario usuario) {
        return firebaseService.obtenerUrlArchivoImagen(usuario.getArchivoAvatar(), usuario.getUsername());
    }
}
