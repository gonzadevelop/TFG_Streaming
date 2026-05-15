package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.pista.ResponsePistaDTO;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CancionMapper {

    @Autowired
    protected FirebaseService firebaseService;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoCancion", source = "archivo")
    @Mapping(target = "usuarios", source = "usuarios")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cancionProductores", ignore = true)
    @Mapping(target = "pistas", ignore = true)
    @Mapping(target = "historialReproducciones", ignore = true)
    @Mapping(target = "duracionSegundos", source = "duracionSegundos")
    @Mapping(target = "topMusicalDiarios", ignore = true)
    public abstract Cancion fromData(String titulo, String archivo, List<Usuario> usuarios, Integer duracionSegundos);

    @Mapping(target = "idPista", source = "pista.id")
    @Mapping(target = "titulo", source = "pista.cancion.titulo")
    @Mapping(target = "artistas", expression = "java(usuarioRepository.findArtistasDeCancion(pista.getCancion().getId()))")
    @Mapping(target = "urlCancion", expression = "java(firebaseService.obtenerUrlArchivoAudio(pista.getCancion().getArchivoCancion()))")
    @Mapping(target = "reproducciones", expression = "java((long) (pista.getCancion() != null && pista.getCancion().getHistorialReproducciones() != null ? pista.getCancion().getHistorialReproducciones().size() : 0))")
    @Mapping(target = "duracionSegundos", expression = "java(pista.getCancion() != null && pista.getCancion().getDuracionSegundos() != null ? pista.getCancion().getDuracionSegundos() : 0)")
    @Mapping(target = "numeroPista", expression = "java(pista.getNumeroPista() == null ? 0 : pista.getNumeroPista())")
    public abstract ResponsePistaDTO toAlbumDto(Pista pista);

    public abstract List<ResponsePistaDTO> toAlbumDtos(List<Pista> pistas);
}