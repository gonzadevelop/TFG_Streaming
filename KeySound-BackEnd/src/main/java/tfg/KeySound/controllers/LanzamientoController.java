package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.lanzamiento.RequestAlbumDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoDTO;
import tfg.KeySound.services.LanzamientoService;

@RestController
@RequestMapping("/lanzamientos")
@RequiredArgsConstructor
public class LanzamientoController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final LanzamientoService lanzamientoService;

    /**
     * Endpoint para que un artista suba un álbum. El artista debe enviar un multipart/form-data con los siguientes campos:
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
        lanzamientoService.subirAlbum(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para que un usuario visualice la información de un lanzamiento (álbum o sencillo).
     * @param lanzamientoId {@link Long}
     * @return {@link ResponseEntity}&lt;{@link ResponseLanzamientoDTO}&gt; Devuelve un status 200 (OK) con la información del lanzamiento
     * @apiNote {@code GET /api/lanzamientos/visualizar/{lanzamientoId}}
     */
    @GetMapping("/visualizar/{lanzamientoId}")
    public ResponseEntity<ResponseLanzamientoDTO> visualizarLanzamiento(@PathVariable Long lanzamientoId) {
        return ResponseEntity.ok(lanzamientoService.visualizarLanzamiento(lanzamientoId));
    }
}
