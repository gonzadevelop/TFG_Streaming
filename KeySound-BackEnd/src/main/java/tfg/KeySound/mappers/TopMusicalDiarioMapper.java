package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.TopMusicalDiario;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ArtistaMapper.class})
public interface TopMusicalDiarioMapper {

    @Mapping(target = "idPista", source = "cancion.id")
    @Mapping(target = "titulo", source = "cancion.titulo")
    @Mapping(target = "artistas", ignore = true)
    @Mapping(target = "urlPortada", ignore = true)
    @Mapping(target = "urlCancion", ignore = true)
    @Mapping(target = "reproducciones", source = "reproduccionesEnElDia")
    @Mapping(target = "duracionSegundos", source = "cancion.duracionSegundos")
    ResponsePistaPlaylistDTO toDto(TopMusicalDiario top);

    List<ResponsePistaPlaylistDTO> toDtos(List<TopMusicalDiario> tops);
}
