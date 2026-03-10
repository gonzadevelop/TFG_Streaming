package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.entitys.Lanzamiento;
import tfg.streamingbackend.entitys.LanzamientoCancion;
import tfg.streamingbackend.model.ReproducirCancionDTO;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LanzamientoCancionMapper {

    @Mapping(source = "cancion", target = "cancion")
    @Mapping(source = "lanzamiento", target = "lanzamiento")
    @Mapping(source = "numeroPista", target = "numeroPista")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlistLanzamientoCanciones", ignore = true)
    LanzamientoCancion toEntity(Cancion cancion, Lanzamiento lanzamiento, Integer numeroPista);

    default List<LanzamientoCancion> toEntityList(List<Cancion> canciones, Lanzamiento lanzamiento) {
        List<LanzamientoCancion> result = new ArrayList<>();
        int numeroPista = 1;
        for (Cancion cancion : canciones) {
            result.add(toEntity(cancion, lanzamiento, numeroPista++));
        }
        return result;
    }

    @Mapping(target = "nombreCancion", source = "nombreCancion")
    @Mapping(target = "urlAudio", source = "urlAudio")
    @Mapping(target = "artistas", source = "artistas")
    @Mapping(target = "urlPortada", source = "urlPortada")
    ReproducirCancionDTO toReproducirCancionDTO(String nombreCancion, String urlAudio, java.util.List<String> artistas, String urlPortada);
}
