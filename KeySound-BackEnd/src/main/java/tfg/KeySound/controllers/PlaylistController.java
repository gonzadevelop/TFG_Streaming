package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.cancion.RequestCancionesPlaylistDTO;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistCompletaDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;
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
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
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
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.playlist.OwnershipRequiredException 403 (FORBIDDEN)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @throws tfg.KeySound.exception.pista.PistaNotFoundException 404 (NOT_FOUND)
     * @throws tfg.KeySound.exception.playlist.PlaylistNotFoundException 404 (NOT_FOUND)
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

    /**
     * Endpoint para obtener las playlists destacadas de KeySound.
     * Estas playlists son seleccionadas por el equipo de la plataforma y se muestran en la página principal de la aplicación para que los usuarios las descubran fácilmente.
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponsePlaylistDTO}&gt;&gt; Devuelve un status 200 (OK) con la lista de playlists destacadas de KeySound
     * @apiNote {@code GET /api/playlists/keysound}
     */
    @GetMapping("/keysound")
    public ResponseEntity<List<ResponsePlaylistDTO>> getKeySoundPlaylists() {
        return ResponseEntity.ok(playlistService.getPlaylists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsePlaylistCompletaDTO> getPlaylistById(
            @PathVariable Long id,
            @RequestParam (required = false) String fecha) {
        return ResponseEntity.ok(playlistService.getPlaylistById(id, fecha));
    }
}
