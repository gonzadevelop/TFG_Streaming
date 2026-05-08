package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import tfg.KeySound.entitys.Playlist;
import tfg.KeySound.entitys.TopMusicalDiario;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistCompletaDTO;
import tfg.KeySound.services.external.FirebaseService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ArtistaMapper.class})
public abstract class TopMusicalDiarioMapper {

    @Autowired
    protected FirebaseService firebaseService;

    /**
     * Mapea una Playlist con sus pistas del ranking diario a un ResponsePlaylistCompletaDTO
     */
    public ResponsePlaylistCompletaDTO toDto(Playlist playlist, List<ResponsePistaPlaylistDTO> pistas) {
        ResponsePlaylistCompletaDTO dto = new ResponsePlaylistCompletaDTO();
        dto.setId(playlist.getId());
        dto.setNombre(playlist.getNombre());
        dto.setDescripcion(playlist.getDescripcion());
        dto.setUsernamePropietario("keysound");
        dto.setUrlPortada(firebaseService.obtenerUrlArchivoImagen(playlist.getFotoPortada(), ""));
        dto.setPistas(pistas);
        dto.setEsPropia(false);
        return dto;
    }
}
