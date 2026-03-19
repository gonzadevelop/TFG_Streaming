package tfg.KeySound.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.model.album.RequestAlbumDTO;
import tfg.KeySound.model.album.ResponseAlbumCompletoDTO;
import tfg.KeySound.services.AlbumService;

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
}
