package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import tfg.KeySound.entitys.Playlist;
import tfg.KeySound.entitys.PlaylistPista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistCompletaDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;
import tfg.KeySound.services.external.FirebaseService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PlaylistMapper {

    @Autowired
    protected FirebaseService firebaseService;

    @Autowired
    protected PistaMapper pistaMapper;

    @Mapping(source = "dto.nombre", target = "nombre")
    @Mapping(source = "dto.esPublica", target = "esPublica")
    @Mapping(source = "usuario", target = "propietario")
    @Mapping(target = "fotoPortada", expression = "java(mapearFotoPortada(dto, usuario))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "descripcion", source = "dto.descripcion", defaultValue = "")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "playlistPistas", ignore = true)
    public abstract Playlist toEntity(RequestPlaylistDTO dto, Usuario usuario);

    protected String mapearFotoPortada(RequestPlaylistDTO dto, Usuario usuario) {
        return dto.getFotoPortada() != null ?
                firebaseService.subirArchivo(dto.getFotoPortada(),
                        "playlist_" + usuario.getId() + "_" + dto.getNombre() + "_")
                : "";
    }

    @Mapping(source = "playlist.id", target = "id")
    @Mapping(source = "playlist.nombre", target = "nombre")
    @Mapping(source = "playlist.descripcion", target = "descripcion")
    @Mapping(target = "urlPortada", expression = "java(firebaseService.obtenerUrlArchivoImagen(playlist.getFotoPortada(), \"\"))")
    public abstract ResponsePlaylistDTO toDto(Playlist playlist);

    public abstract List<ResponsePlaylistDTO> toDtos(List<Playlist> playlists);

    /**
     * Mapea una Playlist completa incluyendo todas sus pistas
     */
    public ResponsePlaylistCompletaDTO toDtoCompleto(Playlist playlist) {
        ResponsePlaylistCompletaDTO dto = new ResponsePlaylistCompletaDTO();
        dto.setId(playlist.getId());
        dto.setNombre(playlist.getNombre());
        dto.setDescripcion(playlist.getDescripcion());
        dto.setUsernamePropietario("keysound");
        dto.setUrlPortada(firebaseService.obtenerUrlArchivoImagen(playlist.getFotoPortada(), ""));
        dto.setPistas(mapearPistasDesdePLaylist(playlist));
        dto.setEsPropia(false);
        return dto;
    }

    /**
     * Extrae y mapea las pistas desde la playlist
     */
    protected List<ResponsePistaPlaylistDTO> mapearPistasDesdePLaylist(Playlist playlist) {
        return playlist.getPlaylistPistas()
                .stream()
                .map(PlaylistPista::getPista)
                .map(pistaMapper::pistaToPlaylistDto)
                .toList();
    }
}