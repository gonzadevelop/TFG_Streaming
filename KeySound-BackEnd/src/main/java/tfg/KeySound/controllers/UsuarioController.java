package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;
import tfg.KeySound.services.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final UsuarioService usuarioService;

    /**
     * Endpoint para que un usuario agregue una canción a su lista de favoritos.
     * Se puede agregar una canción a favoritos, siempre que no esté ya en favoritos
     * @param pistaId {@link Long}
     * @param token {@link String} token JWT del usuario autenticado
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.pista.PistaNotFoundException 404 (NOT_FOUND)
     * @throws tfg.KeySound.exception.playlist.FavoriteAlreadyExistsException 409 (CONFLICT)
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK) si la canción se agrega correctamente a favoritos
     * @apiNote {@code POST /api/usuarios/agregar-cancion-favoritos}
     */
    @PostMapping("/agregar-cancion-favoritos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> agregarCancionAFavoritos(
            @RequestBody Long pistaId,
            @RequestHeader ("Authorization") String token) {
        usuarioService.agregarCancionAFavoritos(pistaId, token.substring(7));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener la información de un usuario.
     * @param username {@link String}
     * @return {@link ResponseEntity}&lt;{@link ResponseUsuarioDTO}&gt; Devuelve un status 200 (OK) con la información del usuario
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @apiNote {@code GET /api/usuarios/visualizar/{username}}
     */
    @GetMapping("/visualizar/{username}")
    public ResponseEntity<ResponseUsuarioDTO> obtenerInfoUsuario(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.obtenerInfoUsuario(username));
    }

    /**
     * Endpoint para que un usuario siga a otro usuario.
     * @param id {@link Long} id del usuario a seguir
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK) si el usuario se sigue correctamente
     * @throws tfg.KeySound.exception.usuario.SelfFollowException 400 (BAD_REQUEST)
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.artista.FollowRestrictionException 403 (FORBIDDEN)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @throws tfg.KeySound.exception.usuario.AlreadyFollowingException 409 (CONFLICT)
     * @apiNote {@code POST /api/usuarios/seguir}
     */
    @PostMapping("/seguir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> seguirUsuario(
            @RequestBody Long id,
            @RequestHeader ("Authorization") String token) {
        usuarioService.seguirUsuario(id, token.substring(7));
        return ResponseEntity.ok().build();
    }
}
