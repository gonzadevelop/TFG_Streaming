package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.favoritos.ResponseFavoritosDTO;
import tfg.KeySound.services.FavoritosService;
import java.security.Principal;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritosController {

    private final FavoritosService favoritosService;

    @GetMapping
    public ResponseEntity<ResponseFavoritosDTO> obtenerFavoritos(Principal principal) {
        return ResponseEntity.ok(favoritosService.obtenerFavoritos(principal.getName()));
    }

    @PostMapping("/{pistaId}")
    public ResponseEntity<Void> añadirFavorito(@PathVariable Long pistaId, Principal principal) {
        favoritosService.añadirFavorito(pistaId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pistaId}")
    public ResponseEntity<Void> eliminarFavorito(@PathVariable Long pistaId, Principal principal) {
        favoritosService.eliminarFavorito(pistaId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
