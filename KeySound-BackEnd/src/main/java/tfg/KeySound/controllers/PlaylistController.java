package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.cancion.RequestCancionesPlaylistDTO;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.services.PlaylistService;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final PlaylistService playlistService;

    /**
     * Endpoint para que un usuario cree una nueva playlist.
     * @param dto {@link RequestPlaylistDTO}
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 201 (CREATED) si la playlist se crea correctamente
     * @apiNote {@code POST /api/playlists/crear}
     */
    @PostMapping("/crear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> crearPlaylist(
            @ModelAttribute RequestPlaylistDTO dto,
            @RequestHeader("Authorization") String token) {
        playlistService.crearPlaylist(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para que un usuario borre una playlist existente.
     * Se puede agregar una o varias canciones a una playlist existente, siempre que el usuario sea el propietario de la playlist
     * @param dto {@link RequestPlaylistDTO}
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 204 (NO_CONTENT) si la playlist se borra correctamente
     * @apiNote {@code POST /api/playlists/agregar-cancion}
     */
    @PostMapping("/agregar-cancion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> agregarCancionAPlaylist(
            @RequestBody RequestCancionesPlaylistDTO dto,
            @RequestHeader ("Authorization") String token) {
        playlistService.agregarCancionesAPlaylist(dto, token.substring(7));
        return ResponseEntity.ok().build();
    }
}
