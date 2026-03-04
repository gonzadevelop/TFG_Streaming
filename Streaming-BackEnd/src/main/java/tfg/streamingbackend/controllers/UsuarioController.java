package tfg.streamingbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.streamingbackend.model.AddCancionesPlaylistDTO;
import tfg.streamingbackend.model.CrearPlaylistDTO;
import tfg.streamingbackend.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Se envía al cliente la URL pública de Firebase Storage para reproducir la canción
    @GetMapping("/reproducir/{cancionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> reproducirCancion(@PathVariable Long cancionId) {
        String urlAudio = usuarioService.obtenerUrlCancion(cancionId);
        return ResponseEntity.ok(urlAudio);
    }

    @PostMapping("/crear-playlist")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> crearPlaylist(
            @ModelAttribute CrearPlaylistDTO dto,
            @RequestHeader("Authorization") String token) {
        usuarioService.crearPlaylist(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Se puede agregar una o varias canciones a una playlist existente, siempre que el usuario sea el propietario de la playlist
    @PostMapping("/agregar-cancion-playlist")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> agregarCancionAPlaylist(
            @RequestBody AddCancionesPlaylistDTO dto,
            @RequestHeader ("Authorization") String token) {
        usuarioService.agregarCancionesAPlaylist(dto, token.substring(7));
        return ResponseEntity.ok().build();
    }

    // Se puede agregar una canción a favoritos, siempre que no esté ya en favoritos
    @PostMapping("/agregar-favoritos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> agregarCancionAFavoritos(
            @RequestBody Long lanzamientoCancionId,
            @RequestHeader ("Authorization") String token) {
        usuarioService.agregarCancionAFavoritos(lanzamientoCancionId, token.substring(7));
        return ResponseEntity.ok().build();
    }
}
