package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
     * Endpoint para obtener la información de un usuario.
     * @param username {@link String}
     * @return {@link ResponseEntity}&lt;{@link ResponseUsuarioDTO}&gt; Devuelve un status 200 (OK) con la información del usuario
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @apiNote {@code GET /api/usuarios/visualizar/{username}}
     */
    @GetMapping("/{username}")
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

    /**
     * Endpoint para que un usuario deje de seguir a un artista.
     * @param id {@link Long} id del usuario a dejar de seguir
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK)
     * @apiNote {@code DELETE /api/usuarios/dejar-seguir/{id}}
     */
    @DeleteMapping("/dejar-seguir/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> dejarDeSeguirUsuario(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        usuarioService.dejarDeSeguirUsuario(id, token.substring(7));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener tu propio nombre de usuario.
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link String}&gt; Devuelve un status 200 (OK) con el nombre de usuario del usuario autenticado
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @apiNote {@code GET /api/usuarios/obtener-username
     */
    @GetMapping("/obtener-username")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> obtenerUsername(@RequestHeader ("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.obtenerUsername(token.substring(7)));
    }

    /**
     * Endpoint para actualizar los datos del perfil del usuario autenticado.
     * @param dto {@link ResponseUsuarioDTO} datos a actualizar (email, biografía)
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link ResponseUsuarioDTO}&gt; Devuelve 200 (OK) con el perfil actualizado
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @apiNote {@code PUT /api/usuarios/perfil}
     */
    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseUsuarioDTO> actualizarPerfil(
            @RequestBody ResponseUsuarioDTO dto,
            @RequestHeader("Authorization") String token) {
        ResponseUsuarioDTO updated = usuarioService.actualizarPerfil(dto, token.substring(7));
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint para actualizar el avatar del usuario autenticado.
     * Si ya tenía un avatar previo, se elimina de Firebase antes de subir el nuevo.
     * @param avatar {@link MultipartFile} nueva imagen de perfil
     * @param token {@link String} token JWT del usuario autenticado
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve 200 (OK) si se actualiza correctamente
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @apiNote {@code POST /api/usuarios/avatar}
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> actualizarAvatar(
            @RequestPart("avatar") MultipartFile avatar,
            @RequestHeader("Authorization") String token) {
        usuarioService.actualizarAvatar(avatar, token.substring(7));
        return ResponseEntity.ok().build();
    }

}
