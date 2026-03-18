package tfg.KeySound.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.pista.ResponsePistaDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CancionMapper {

    @Mapping(target = "idAlbum", source = "id")
    @Mapping(target = "titulo", source = "cancion.titulo")
    @Mapping(target = "urlPortada", source = "album.archivoPortada")
    @Mapping(target = "artistas", expression = "java(pista.getCancion().getUsuarios().stream().map(u -> u.getUsername()).toList())")
    @Mapping(target = "urlCancion", source = "cancion.archivoCancion")
    @Mapping(target = "reproducciones", expression = "java((long) (pista.getCancion().getHistorialReproducciones() != null ? pista.getCancion().getHistorialReproducciones().size() : 0))")
    ResponseCancionArtistaDTO toDto(Pista pista);

    List<ResponseCancionArtistaDTO> toDtos(List<Pista> pistas);

    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "archivoCancion", source = "archivo")
    @Mapping(target = "usuarios", source = "usuarios")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cancionProductores", ignore = true)
    @Mapping(target = "pistas", ignore = true)
    @Mapping(target = "historialReproducciones", ignore = true)
    @Mapping(target = "duracionSegundos", source = "duracionSegundos")
    @Mapping(target = "topMusicalDiarios", ignore = true)
    Cancion fromData(String titulo, String archivo, List<Usuario> usuarios, Integer duracionSegundos);

    @Mapping(target = "titulo", source = "pista.cancion.titulo")
    @Mapping(target = "artistas", expression = "java(pista.getCancion() == null || pista.getCancion().getUsuarios() == null ? java.util.Collections.emptyList() : pista.getCancion().getUsuarios().stream().map(u -> u.getUsername()).toList())")
    @Mapping(target = "urlCancion", source = "url")
    @Mapping(target = "reproducciones", expression = "java((long) (pista.getCancion() != null && pista.getCancion().getHistorialReproducciones() != null ? pista.getCancion().getHistorialReproducciones().size() : 0))")
    @Mapping(target = "duracionSegundos", expression = "java(pista.getCancion() != null && pista.getCancion().getDuracionSegundos() != null ? pista.getCancion().getDuracionSegundos() : 0)")
    @Mapping(target = "numeroPista", expression = "java(pista.getNumeroPista() == null ? 0 : pista.getNumeroPista())")
    ResponsePistaDTO toAlbumDto(Pista pista, String url);
}
