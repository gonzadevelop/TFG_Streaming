package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PistaMapper {

    @Autowired
    protected FirebaseService firebaseService;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected HistorialReproduccionesRepository historialReproduccionesRepository;

    @Mapping(source = "cancion", target = "cancion")
    @Mapping(source = "album", target = "album")
    @Mapping(source = "numeroPista", target = "numeroPista")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlistPistas", ignore = true)
    public abstract Pista toEntity(Cancion cancion, Album album, Integer numeroPista);

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "duracionSegundos", source = "duracionSegundos")
    @Mapping(target = "urlCancion", expression = "java(firebaseService.obtenerUrlArchivoAudio(cancion.getArchivoCancion()))")
    @Mapping(target = "reproducciones", expression = "java(cancion.getHistorialReproducciones() != null ? cancion.getHistorialReproducciones().size() : 0)")
    @Mapping(target = "artistas", expression = "java(usuarioRepository.findArtistasDeCancion(cancion.getId()))")
    @Mapping(target = "idPista", ignore = true)
    @Mapping(target = "idAlbum", ignore = true)
    @Mapping(target = "urlPortada", ignore = true)
    protected abstract ResponsePistaHomeDTO toDtoBase(Cancion cancion);

    public ResponsePistaHomeDTO toDto(Cancion cancion) {
        if (cancion == null) return null;

        ResponsePistaHomeDTO dto = toDtoBase(cancion);

        // Acceso directo a la primera pista sin abrir un Stream
        var pistas = cancion.getPistas();
        if (pistas != null && !pistas.isEmpty()) {
            Pista primeraPista = pistas.iterator().next();
            Album album = primeraPista.getAlbum();

            if (album != null) {
                dto.setIdPista(primeraPista.getId());
                dto.setIdAlbum(album.getId());
                dto.setUrlPortada(firebaseService.obtenerUrlArchivoImagen(album.getArchivoPortada(), album.getTitulo()));
            }
        }

        return dto;
    }

    public List<ResponsePistaHomeDTO> toDtos(List<Cancion> canciones) {
        if (canciones == null) return null;
        return canciones.stream()
                .map(this::toDto)
                .toList();
    }

    public List<ResponsePistaHomeDTO> toDtosConReproduccionesDelUsuario(List<Cancion> canciones, Long usuarioId) {
        if (canciones == null) return null;
        return canciones.stream()
                .map(cancion -> {
                    ResponsePistaHomeDTO dto = toDto(cancion);
                    // Contar reproducciones específicas del usuario para esta canción
                    int reproduccionesDelUsuario = historialReproduccionesRepository
                            .countReproduccionesByUsuarioAndCancion(usuarioId, cancion.getId());
                    dto.setReproducciones(reproduccionesDelUsuario);
                    return dto;
                })
                .toList();
    }

    @Mapping(target = "idPista", source = "pista.id")
    @Mapping(target = "titulo", source = "pista.cancion.titulo")
    @Mapping(target = "urlPortada", expression = "java(firebaseService.obtenerUrlArchivoImagen(pista.getAlbum().getArchivoPortada(), \"\"))")
    @Mapping(target = "urlCancion", expression = "java(firebaseService.obtenerUrlArchivoAudio(pista.getCancion().getArchivoCancion()))")
    @Mapping(target = "artistas", expression = "java(usuarioRepository.findArtistasDeCancion(pista.getCancion().getId()))")
    @Mapping(target = "reproducciones", ignore = true)
    @Mapping(target = "duracionSegundos", source = "pista.cancion.duracionSegundos")
    public abstract ResponsePistaPlaylistDTO pistaToPlaylistDto(Pista pista);

    public abstract List<ResponsePistaPlaylistDTO> pistasToPlaylistDtos(List<Pista> pistas);
}
