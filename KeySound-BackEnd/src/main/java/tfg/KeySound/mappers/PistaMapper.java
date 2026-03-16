package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Lanzamiento;
import tfg.KeySound.entitys.Pista;

@Mapper(componentModel = "spring")
public interface PistaMapper {

    @Mapping(source = "cancion", target = "cancion")
    @Mapping(source = "lanzamiento", target = "lanzamiento")
    @Mapping(source = "numeroPista", target = "numeroPista")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlistPistas", ignore = true)
    Pista toEntity(Cancion cancion, Lanzamiento lanzamiento, Integer numeroPista);
}
