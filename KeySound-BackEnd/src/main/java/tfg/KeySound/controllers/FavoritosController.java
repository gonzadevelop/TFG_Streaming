package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.pista.ResponsePistaAlbumDTO;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.services.FavoritosService;
import java.util.List;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritosController {

    private final FavoritosService favoritosService;

    @GetMapping
    public ResponseEntity<List<ResponsePistaPlaylistDTO>> obtenerFavoritos(@RequestHeader ("Authorization") String token) {
        return ResponseEntity.ok(favoritosService.obtenerFavoritos(token.substring(7)));
    }

    @GetMapping("/album")
    public ResponseEntity<List<ResponsePistaAlbumDTO>> obtenerFavoritosAlbum(@RequestHeader ("Authorization") String token) {
        return ResponseEntity.ok(favoritosService.obtenerFavoritosAlbum(token.substring(7)));
    }

    @PostMapping("/{pistaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> anadirFavorito(@PathVariable Long pistaId, @RequestHeader ("Authorization") String token) {
        favoritosService.anadirFavorito(pistaId, token.substring(7));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pistaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> eliminarFavorito(@PathVariable Long pistaId, @RequestHeader ("Authorization") String token) {
        favoritosService.eliminarFavorito(pistaId, token.substring(7));
        return ResponseEntity.noContent().build();
    }
}
