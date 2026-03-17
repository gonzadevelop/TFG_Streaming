package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Lanzamiento;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoArtistaDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoDTO;
import tfg.KeySound.model.lanzamiento.ResponseMiLanzamientoDTO;
import tfg.KeySound.model.pista.ResponsePistaDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface LanzamientoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "urlPortada", source = "archivoPortada")
    @Mapping(target = "anioLanzamiento", source = "lanzamiento.fechaLanzamiento.year")
    @Mapping(target = "tipo", source = "tipo")
    ResponseLanzamientoArtistaDTO toDto(Lanzamiento lanzamiento);

    List<ResponseLanzamientoArtistaDTO> toDtos(List<Lanzamiento> lanzamientos);

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoPortada", source = "archivoPortada")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pistas", ignore = true)
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "esBorrador", constant = "true")
    Lanzamiento createLanzamiento(String titulo, String archivoPortada, String tipo, Usuario usuario, LocalDateTime fechaLanzamiento);

    @Mapping(target = "nombreLanzamiento", source = "lanzamiento.titulo")
    @Mapping(target = "portada", source = "urlPortada")
    @Mapping(target = "anioLanzamiento", expression = "java(lanzamiento.getFechaLanzamiento() != null ? lanzamiento.getFechaLanzamiento().getYear() : 0)")
    @Mapping(target = "duracionTotalSegundos", expression = "java(canciones == null ? 0 : canciones.stream().mapToInt(c -> c.getDuracionSegundos()).sum())")
    @Mapping(target = "numCanciones", expression = "java(canciones == null ? 0 : canciones.size())")
    @Mapping(target = "tipo", source = "lanzamiento.tipo")
    @Mapping(target = "canciones", source = "canciones")
    @Mapping(target = "artista", source = "lanzamiento.usuario.username")
    ResponseLanzamientoDTO toResponseDto(Lanzamiento lanzamiento, List<ResponsePistaDTO> canciones, String urlPortada);

    @Mapping(target = "idLanzamiento", source = "id")
    @Mapping(target = "nombreAlbum", source = "titulo")
    @Mapping(target = "portada", ignore = true)
    @Mapping(target = "esBorrador", source = "esBorrador")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "numeroCanciones", expression = "java(lanzamiento.getPistas() == null ? 0 : lanzamiento.getPistas().size())")
    @Mapping(target = "tipo", source = "tipo")
    ResponseMiLanzamientoDTO toMiLanzamientoDto(Lanzamiento lanzamiento);

    List<ResponseMiLanzamientoDTO> toMisLanzamientosDtos(List<Lanzamiento> lanzamientos);
}
