package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.lanzamiento.ResponseMiLanzamientoDTO;
import tfg.KeySound.model.usuario.ResponseArtistaDTO;
import tfg.KeySound.services.ArtistaService;

import java.util.List;

@RestController
@RequestMapping("/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final ArtistaService artistaService;

    /**
     * Endpoint para obtener la información de un artista.
     * @param username {@link String}
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link ResponseArtistaDTO}&gt; Devuelve un status 200 (OK)
     * @apiNote {@code GET /api/artistas/visualizar/{username}}
     */
    @GetMapping("/visualizar/{username}")
    public ResponseEntity<ResponseArtistaDTO> obtenerInfoArtista(
            @PathVariable String username,
            @RequestHeader(value = "Authorization") String token) {
        String tokenSinBearer = token != null && token.startsWith("Bearer ")
                ? token.substring(7)
                : "";
        return ResponseEntity.ok(artistaService.obtenerInfoArtista(username, tokenSinBearer));
    }

    /**
     * Endpoint para obtener los lanzamientos de un artista.
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponseMiLanzamientoDTO}&gt;&gt; Devuelve un status 200 (OK)
     * @apiNote {@code GET /api/artistas/mis-lanzamientos}
     */
    @GetMapping("/mis-lanzamientos")
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<List<ResponseMiLanzamientoDTO>> obtenerMisLanzamientos(
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(artistaService.obtenerMisLanzamientos(token.substring(7)));
    }

    /**
     * Endpoint para publicar un lanzamiento.
     * @param idLanzamiento {@link Long}
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK)
     * @apiNote {@code PATCH /api/artistas/publicar/{idLanzamiento}}
     */
    @PatchMapping("/publicar/{idLanzamiento}")
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> publicarLanzamiento(
            @PathVariable Long idLanzamiento,
            @RequestHeader(value = "Authorization") String token) {
        artistaService.publicarLanzamiento(idLanzamiento, token.substring(7));
        return ResponseEntity.ok().build();
    }
}
