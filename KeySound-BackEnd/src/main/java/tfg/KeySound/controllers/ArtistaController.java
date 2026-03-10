package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.ArtistaDTO;
import tfg.KeySound.model.CrearAlbumDTO;
import tfg.KeySound.model.CrearSencilloDTO;
import tfg.KeySound.services.ArtistaService;

@RestController
@RequestMapping("/api/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService artistaService;

    @PostMapping(value = "/subir-sencillo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> subirSencillo(
            @RequestHeader("Authorization") String token,
            @ModelAttribute CrearSencilloDTO dto) {
        // substring(7) para eliminar "Bearer " del token
        artistaService.subirSencillo(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/subir-album", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> subirAlbum(
            @RequestHeader("Authorization") String token,
            @ModelAttribute CrearAlbumDTO dto) {
        // substring(7) para eliminar "Bearer " del token
        artistaService.subirAlbum(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/borrar-sencillo/{sencilloId}")
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> borrarSencillo(
            @RequestHeader("Authorization") String token,
            @PathVariable Long sencilloId) {
        // substring(7) para eliminar "Bearer " del token
        artistaService.eliminarSencillo(sencilloId, token.substring(7));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/info-artista/{username}")
    public ResponseEntity<ArtistaDTO> obtenerInfoArtista(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(artistaService.obtenerInfoArtista(username, token.substring(7)));
    }
}
