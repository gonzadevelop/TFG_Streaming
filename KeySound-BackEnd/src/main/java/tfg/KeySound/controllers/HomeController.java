package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.home.HomeDTO;
import tfg.KeySound.services.HomeService;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final HomeService homeService;

    /**
     * Endpoint para que un usuario visualice la información de su home. La información incluye playlists destacadas, artistas seguidos, albums seguidos, próximos lanzamientos y canciones más escuchadas.
     * @param token {@link String} Token JWT del usuario
     * @return {@link ResponseEntity}&lt;{@link HomeDTO}&gt; Devuelve un status 200 (OK) con la información de la home del usuario
     * @apiNote {@code GET /api/home/visualizar}
     */
    @GetMapping("/visualizar")
    public ResponseEntity<HomeDTO> getHome(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(homeService.getHome(token.substring(7)));
    }
}
