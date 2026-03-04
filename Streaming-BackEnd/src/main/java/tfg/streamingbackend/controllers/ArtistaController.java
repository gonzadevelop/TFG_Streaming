package tfg.streamingbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.streamingbackend.model.CrearCancionDTO;
import tfg.streamingbackend.services.ArtistaService;

@RestController
@RequestMapping("/api/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService artistaService;

    @PostMapping(value = "/subir-sencillo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTISTA')")
    public ResponseEntity<Void> subirCancion(
            @RequestHeader("Authorization") String token,
            @ModelAttribute CrearCancionDTO dto) {
        // substring(7) para eliminar "Bearer " del token
        artistaService.subirCancion(dto, token.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
