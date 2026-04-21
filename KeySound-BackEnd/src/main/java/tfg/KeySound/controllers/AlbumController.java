package tfg.KeySound.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.model.album.RequestAlbumDTO;
import tfg.KeySound.model.album.ResponseAlbumCompletoDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.album.ResponseProximoAlbumDTO;
import tfg.KeySound.services.AlbumService;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final AlbumService albumService;

    /**
     * Endpoint para que un artista suba un álbum (álbum o sencillo). El álbum se crea en estado "borrador" y debe ser publicado posteriormente.
     * @param token {@link String}
     * @param dto {@link RequestAlbumDTO} con los datos del álbum a subir
     * @param portada {@link MultipartFile} con la imagen de portada del álbum
     * @param archivos {@link List}&lt;{@link MultipartFile}&gt; con las pistas de audio del álbum
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 201 (Created) si se sube correctamente
     * @throws tfg.KeySound.exception.archivo.AudioProcessingException 400 (BAD_REQUEST)
     * @throws tfg.KeySound.exception.cancion.InvalidFormatFileException 400 (BAD_REQUEST)
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @throws tfg.KeySound.exception.cancion.CancionNotFoundException 404 (NOT_FOUND)
     * @throws org.springframework.web.multipart.MaxUploadSizeExceededException 413 (PAYLOAD_TOO_LARGE)
     * @throws   javax.naming.SizeLimitExceededException 413 (PAYLOAD_TOO_LARGE)
     * @throws   org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException 413 (PAYLOAD_TOO_LARGE)
     * @apiNote {@code POST /api/albums/crear}
     */
    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> subirAlbum(
            @RequestHeader("Authorization") String token,
            @RequestPart("datos") RequestAlbumDTO dto,
            @RequestPart("portada") MultipartFile portada,
            @RequestPart("archivos") List<MultipartFile> archivos) {
        albumService.subirAlbum(dto, portada, archivos, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para que un usuario visualice la información de un album (álbum o sencillo).
     * @param albumId {@link Long}
     * @return {@link ResponseEntity}&lt;{@link ResponseAlbumCompletoDTO}&gt; Devuelve un status 200 (OK) con la información del album
     * @throws tfg.KeySound.exception.album.AlbumNotFoundException 404 (NOT_FOUND)
     * @throws tfg.KeySound.exception.cancion.CancionNotFoundException 404 (NOT_FOUND)
     * @apiNote {@code GET /api/albums/visualizar/{albumId}}
     */
    @GetMapping("/visualizar/{albumId}")
    public ResponseEntity<ResponseAlbumCompletoDTO> visualizarAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.visualizarAlbum(albumId));
    }

    /**
     * Endpoint para que un artista publique un álbum. El álbum debe estar previamente subido y en estado "borrador".
     * @param token {@link String}
     * @param albumId {@link Long}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK) si se publica correctamente
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @apiNote {@code PUT /api/albums/publicar/{albumId}}
     */
    @PutMapping("/publicar/{albumId}")
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> publicarAlbum(
            @RequestHeader("Authorization") String token,
            @PathVariable Long albumId) {
        albumService.publicarAlbum(albumId, token.substring(7));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener los próximos lanzamientos de álbumes (álbumes o sencillos) ordenados por fecha de lanzamiento. Solo se muestran los álbumes que no son borradores y con fecha de lanzamiento en el futuro.
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponseAlbumDTO}&gt;&gt; Devuelve un status 200 (OK) con la lista de próximos lanzamientos
     * @apiNote {@code GET /api/albums/proximos-lanzamientos}
     */
    @GetMapping("/proximos-lanzamientos")
    public ResponseEntity<List<ResponseProximoAlbumDTO>> obtenerProximosLanzamientos() {
        return ResponseEntity.ok(albumService.obtenerProximosLanzamientos());
    }

    /**
     * Endpoint para obtener las novedades de la semana en álbumes (álbumes o sencillos) ordenados por fecha de lanzamiento. Solo se muestran los álbumes que no son borradores y con fecha de lanzamiento en el pasado o presente, y que se hayan lanzado en la última semana.
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponseAlbumDTO}&gt;&gt; Devuelve un status 200 (OK) con la lista de novedades de la semana
     * @apiNote {@code GET /api/albums/novedades-semana}
     */
    @GetMapping("/novedades-semana")
    public ResponseEntity<List<ResponseAlbumDTO>> obtenerNovedades() {
        return ResponseEntity.ok(albumService.obtenerNovedades());
    }
}
