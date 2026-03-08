package tfg.streamingbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.entitys.Lanzamiento;
import tfg.streamingbackend.entitys.LanzamientoCancion;

@Mapper(componentModel = "spring")
public interface LanzamientoCancionMapper {

    @Mapping(source = "cancion", target = "cancion")
    @Mapping(source = "lanzamiento", target = "lanzamiento")
    @Mapping(source = "numeroPista", target = "numeroPista")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlistLanzamientoCanciones", ignore = true)
    LanzamientoCancion toEntity(Cancion cancion, Lanzamiento lanzamiento, Integer numeroPista);
}


