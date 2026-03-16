package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.usuario.ResponseArtistaDTO;
import tfg.KeySound.services.ArtistaService;

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
}
