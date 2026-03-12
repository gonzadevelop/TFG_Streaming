package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.usuario.ResponseArtistaDTO;
import tfg.KeySound.model.lanzamiento.RequestAlbumDTO;
import tfg.KeySound.model.lanzamiento.RequestSencilloDTO;
import tfg.KeySound.services.ArtistaService;

@RestController
@RequestMapping("/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService artistaService;

    /**
     * Endpoint para que un artista suba un sencillo.
     * @param token {@link String}
     * @param dto {@link RequestSencilloDTO}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 201 (CREATED) si se sube correctamente
     */
    @PostMapping(value = "/subir-sencillo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> subirSencillo(
            @RequestHeader("Authorization") String token,
            @ModelAttribute RequestSencilloDTO dto) {
        // substring(7) para eliminar "Bearer " del token
        artistaService.subirSencillo(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * Endpoint para que un artista suba un álbum. El artista debe enviar un multipart/form-data con los siguientes campos:
     * @param token {@link String}
     * @param dto {@link RequestAlbumDTO}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 201 (CREATED) si se sube correctamente
     */
    @PostMapping(value = "/subir-album", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> subirAlbum(
            @RequestHeader("Authorization") String token,
            @ModelAttribute RequestAlbumDTO dto) {
        // substring(7) para eliminar "Bearer " del token
        artistaService.subirAlbum(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para que un artista borre un sencillo. El artista debe ser el propietario del sencillo para poder borrarlo.
     * @param token {@link String}
     * @param sencilloId {@link Long}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt; Devuelve un status 204 (NO_CONTENT) si se borra correctamente
     */
    @DeleteMapping("/borrar-sencillo/{sencilloId}")
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> borrarSencillo(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sencilloId) {
        // substring(7) para eliminar "Bearer " del token
        artistaService.eliminarSencillo(sencilloId, token.substring(7));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Endpoint para que un artista borre un álbum. El artista debe ser el propietario del álbum para poder borrarlo.
     * @param username {@link String}
     * @param token {@link String}
     * @return {@link ResponseEntity}&lt;{@link ResponseArtistaDTO}&gt; Devuelve un status 200 (OK)
     */
    @GetMapping("/info-artista/{username}")
    public ResponseEntity<ResponseArtistaDTO> obtenerInfoArtista(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(artistaService.obtenerInfoArtista(username, token.substring(7)));
    }
}
