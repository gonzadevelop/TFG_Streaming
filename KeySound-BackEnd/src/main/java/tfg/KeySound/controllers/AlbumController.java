package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.album.RequestAlbumDTO;
import tfg.KeySound.model.album.ResponseAlbumCompletoDTO;
import tfg.KeySound.services.AlbumService;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final AlbumService albumService;

    /**
     * Endpoint para que un artista cree un álbum. El artista debe enviar un multipart/form-data con los siguientes campos:
     * @param token {@link String}
     * @param dto {@link RequestAlbumDTO}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 201 (CREATED) si se sube correctamente
     * @apiNote {@code POST /api/artistas/crear}
     */
    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> subirAlbum(
            @RequestHeader("Authorization") String token,
            @ModelAttribute RequestAlbumDTO dto) {
        // substring(7) para eliminar "Bearer " del token
        albumService.subirAlbum(dto, token.substring(7));
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
}
