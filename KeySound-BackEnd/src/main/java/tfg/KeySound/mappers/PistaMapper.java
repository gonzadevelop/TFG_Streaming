package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;

@Mapper(componentModel = "spring")
public interface PistaMapper {

    @Mapping(source = "cancion", target = "cancion")
    @Mapping(source = "album", target = "album")
    @Mapping(source = "numeroPista", target = "numeroPista")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "playlistPistas", ignore = true)
    Pista toEntity(Cancion cancion, Album album, Integer numeroPista);
}
