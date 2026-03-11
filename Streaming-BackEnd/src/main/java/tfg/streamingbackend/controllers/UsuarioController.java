package tfg.streamingbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.streamingbackend.model.AddCancionesPlaylistDTO;
import tfg.streamingbackend.model.CrearPlaylistDTO;
import tfg.streamingbackend.model.ReproducirCancionDTO;
import tfg.streamingbackend.model.UsuarioDTO;
import tfg.streamingbackend.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Se envía al cliente la URL pública de Firebase Storage para reproducir la canción, junto con el nombre de la canción, los artistas y la url de la portada.
    @GetMapping("/reproducir/{lanzamientoCancionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReproducirCancionDTO> reproducirCancion(@PathVariable Long lanzamientoCancionId) {
        return ResponseEntity.ok(usuarioService.obtenerUrlCancion(lanzamientoCancionId));
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

    @GetMapping("/info-usuario/{username}")
    public ResponseEntity<UsuarioDTO> obtenerInfoUsuario(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.obtenerInfoUsuario(username));
    }
}
