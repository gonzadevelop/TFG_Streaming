package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.services.CancionService;

import java.util.List;

@RestController
@RequestMapping("/canciones")
@RequiredArgsConstructor
public class CancionController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final CancionService cancionService;

    /**
     * Endpoint para que un usuario reproduzca una canción.
     * Se registra la reproducción de la canción en el historial del usuario.
     * SE DEBE EJECUTAR EL ENDPOINT CADA VEZ QUE EL USUARIO LLEGUE AL SEGUNDO 30 DE LA CANCION!!!!!
     * @param pistaId {@link Long}
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK) si la canción se reproduce correctamente
     * @apiNote {@code POST /api/canciones/reproducir}
     */
    @PostMapping("/reproducir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reproducirCancion(@RequestBody Long pistaId,
                                                 @RequestHeader ("Authorization") String token) {
        cancionService.reproducir(pistaId, token.substring(7));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener las canciones más reproducidas por el usuario autenticado.
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponsePistaHomeDTO}&gt;&gt; Devuelve una lista de canciones más reproducidas por el usuario
     * @apiNote {@code GET /api/canciones/mis-canciones-mas-reproducidas}
     */
    @GetMapping("/mis-canciones-mas-reproducidas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ResponsePistaHomeDTO>> obtenerMisCancionesMasReproducidas(
            @RequestHeader ("Authorization") String token) {
        return ResponseEntity.ok(cancionService.obtenerMisCancionesMasReproducidas(token.substring(7)));
    }
}
