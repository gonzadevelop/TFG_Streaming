package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.cancion.RequestCancionesPlaylistDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoDTO;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;
import tfg.KeySound.services.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Endpoint para que un usuario cree una nueva playlist.
     * @param dto {@link RequestPlaylistDTO}
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 201 (CREATED) si la playlist se crea correctamente
     */
    @PostMapping("/crear-playlist")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> crearPlaylist(
            @ModelAttribute RequestPlaylistDTO dto,
            @RequestHeader("Authorization") String token) {
        usuarioService.crearPlaylist(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para que un usuario borre una playlist existente.
     * Se puede agregar una o varias canciones a una playlist existente, siempre que el usuario sea el propietario de la playlist
     * @param dto {@link RequestPlaylistDTO}
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 204 (NO_CONTENT) si la playlist se borra correctamente
     */
    @PostMapping("/agregar-cancion-playlist")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> agregarCancionAPlaylist(
            @RequestBody RequestCancionesPlaylistDTO dto,
            @RequestHeader ("Authorization") String token) {
        usuarioService.agregarCancionesAPlaylist(dto, token.substring(7));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para que un usuario agregue una canción a su lista de favoritos.
     * Se puede agregar una canción a favoritos, siempre que no esté ya en favoritos
     * @param lanzamientoCancionId {@link Long}
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK) si la canción se agrega correctamente a favoritos
     */
    @PostMapping("/agregar-favoritos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> agregarCancionAFavoritos(
            @RequestBody Long lanzamientoCancionId,
            @RequestHeader ("Authorization") String token) {
        usuarioService.agregarCancionAFavoritos(lanzamientoCancionId, token.substring(7));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener la información de un usuario.
     * @param username {@link String}
     * @return {@link ResponseEntity}&lt;{@link ResponseUsuarioDTO}&gt; Devuelve un status 200 (OK) con la información del usuario
     */
    @GetMapping("/info-usuario/{username}")
    public ResponseEntity<ResponseUsuarioDTO> obtenerInfoUsuario(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.obtenerInfoUsuario(username));
    }

    /**
     * Endpoint para que un usuario visualice la información de un lanzamiento (álbum o sencillo).
     * @param lanzamientoId {@link Long}
     * @return {@link ResponseEntity}&lt;{@link ResponseLanzamientoDTO}&gt; Devuelve un status 200 (OK) con la información del lanzamiento
     */
    @GetMapping("/visualizar-lanzamiento/{lanzamientoId}")
    public ResponseEntity<ResponseLanzamientoDTO> visualizarLanzamiento(@PathVariable Long lanzamientoId) {
        return ResponseEntity.ok(usuarioService.visualizarLanzamiento(lanzamientoId));
    }
}
