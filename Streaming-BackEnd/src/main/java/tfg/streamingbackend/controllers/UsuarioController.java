package tfg.streamingbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
}
