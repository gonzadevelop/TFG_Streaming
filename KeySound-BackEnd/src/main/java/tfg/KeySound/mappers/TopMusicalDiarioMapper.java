package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.TopMusicalDiario;
import tfg.KeySound.model.pista.ResponsePistaTopPlaylistDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ArtistaMapper.class})
public interface TopMusicalDiarioMapper {

    @Mapping(target = "idCancion", source = "cancion.id")
    @Mapping(target = "titulo", source = "cancion.titulo")
    @Mapping(target = "artistas", source = "cancion.usuarios")
    @Mapping(target = "urlPortada", ignore = true)
    @Mapping(target = "urlCancion", ignore = true)
    @Mapping(target = "reproducciones", source = "reproduccionesEnElDia")
    @Mapping(target = "duracionSegundos", source = "cancion.duracionSegundos")
    @Mapping(target = "numeroPista", source = "posicionEnElDia")
    ResponsePistaTopPlaylistDTO toDto(TopMusicalDiario top);

    List<ResponsePistaTopPlaylistDTO> toDtos(List<TopMusicalDiario> tops);
}

