package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.estadisticas.EstadisticasDTO;
import tfg.KeySound.model.estadisticas.TopAlbumDTO;
import tfg.KeySound.model.estadisticas.TopArtistaDTO;
import tfg.KeySound.model.estadisticas.TopCancionDTO;
import tfg.KeySound.services.EstadisticasService;

import java.util.List;

@RestController
@RequestMapping("/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    /**
     * Devuelve las estadísticas mensuales del usuario autenticado:
     * minutos escuchados, top 5 canciones, top 5 artistas y top 5 álbumes del mes.
     *
     * @param token Token JWT del usuario (cabecera Authorization)
     * @return {@link EstadisticasDTO} con todas las estadísticas del mes
     * @apiNote {@code GET /api/estadisticas/mes}
     */
    @GetMapping("/mes")
    public ResponseEntity<EstadisticasDTO> getEstadisticasMes(
            @RequestHeader("Authorization") String token) {
        String tokenSinBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(estadisticasService.getEstadisticasMensuales(tokenSinBearer));
    }

    @GetMapping("/minutos-mes")
    public ResponseEntity<Long> getMinutosMes(
            @RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(estadisticasService.getMinutosMensuales(jwt));
    }

    @GetMapping("/total-reproducciones-mes")
    public ResponseEntity<Long> getTotalReproduccionesMes(
            @RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(estadisticasService.getTotalReproduccionesMes(jwt));
    }

    @GetMapping("/top-canciones")
    public ResponseEntity<List<TopCancionDTO>> getTopCanciones(
            @RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(estadisticasService.getTopCanciones(jwt));
    }

    @GetMapping("/top-artistas")
    public ResponseEntity<List<TopArtistaDTO>> getTopArtistas(
            @RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(estadisticasService.getTopArtistas(jwt));
    }

    @GetMapping("/top-albumes")
    public ResponseEntity<List<TopAlbumDTO>> getTopAlbumes(
            @RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(estadisticasService.getTopAlbumes(jwt));
    }
}

