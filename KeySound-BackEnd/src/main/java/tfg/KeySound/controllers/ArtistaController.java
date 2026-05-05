package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.album.ResponseMiAlbumDTO;
import tfg.KeySound.model.artista.ResponseArtistaDTO;
import tfg.KeySound.model.artista.ResponseArtistaHomeDTO;
import tfg.KeySound.services.ArtistaService;

import java.util.List;

@RestController
@RequestMapping("/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final ArtistaService artistaService;

    /**
     * Endpoint para obtener la información de un artista.
     * @param username {@link String}
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link ResponseArtistaDTO}&gt; Devuelve un status 200 (OK)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @apiNote {@code GET /api/artistas/{username}}
     */
    @GetMapping("/{username}")
    public ResponseEntity<ResponseArtistaDTO> obtenerInfoArtista(
            @PathVariable String username,
            @RequestHeader(value = "Authorization") String token) {
        String tokenSinBearer = token != null && token.startsWith("Bearer ")
                ? token.substring(7)
                : "";
        return ResponseEntity.ok(artistaService.obtenerInfoArtista(username, tokenSinBearer));
    }

    /**
     * Endpoint para obtener los albums de un artista.
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponseMiAlbumDTO}&gt;&gt; Devuelve un status 200 (OK)
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @apiNote {@code GET /api/artistas/mis-albums}
     */
    @GetMapping("/mis-albums")
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<List<ResponseMiAlbumDTO>> obtenerMisAlbums(
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(artistaService.obtenerMisAlbums(token.substring(7)));
    }

    /**
     * Endpoint para publicar un album.
     * @param idAlbum {@link Long}
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 200 (OK)
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.album.AlbumNotFoundException 404 (NOT_FOUND)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @apiNote {@code PATCH /api/artistas/publicar/{idAlbum}}
     */
    @PatchMapping("/publicar/{idAlbum}")
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> publicarAlbum(
            @PathVariable Long idAlbum,
            @RequestHeader(value = "Authorization") String token) {
        artistaService.publicarAlbum(idAlbum, token.substring(7));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener los artistas que sigo.
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponseArtistaHomeDTO}&gt;&gt; Devuelve un status 200 (OK)
     * @throws javax.naming.AuthenticationException 401 (UNAUTHORIZED)
     * @throws tfg.KeySound.exception.auth.UsernameNotFoundException 404 (NOT_FOUND)
     * @apiNote {@code GET /api/artistas/artistas-que-sigo}
     */
    @GetMapping("/artistas-que-sigo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ResponseArtistaHomeDTO>> obtenerArtistasQueSigo(
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(artistaService.obtenerArtistasQueSigo(token.substring(7)));
    }

    /**
     * Endpoint para buscar artistas por nombre de usuario.
     * @param q {@link String} término de búsqueda
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponseArtistaHomeDTO}&gt;&gt;
     * @apiNote {@code GET /api/artistas/buscar?q=término}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ResponseArtistaHomeDTO>> buscarArtistas(@RequestParam String q) {
        return ResponseEntity.ok(artistaService.buscarArtistas(q));
    }
}
