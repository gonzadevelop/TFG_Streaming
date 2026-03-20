package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.model.album.RequestAlbumDTO;
import tfg.KeySound.model.album.ResponseAlbumCompletoDTO;
import tfg.KeySound.model.playlist.ResponseKeySoundPlaylistDTO;
import tfg.KeySound.services.AlbumService;
import tfg.KeySound.services.PlaylistKeysoundService;

import java.util.List;

@RestController
@RequestMapping("/KeySoundPlaylists")
@RequiredArgsConstructor
public class PlaylistKeysoundController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final PlaylistKeysoundService playlistKeysoundService;

    /**
     * Endpoint para obtener las playlists destacadas de KeySound.
     * Estas playlists son seleccionadas por el equipo de la plataforma y se muestran en la página principal de la aplicación para que los usuarios las descubran fácilmente.
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponseKeySoundPlaylistDTO}&gt;&gt; Devuelve una lista de playlists destacadas con un status 200 (OK)
     * @apiNote {@code GET /api/KeySoundPlaylists}
     */
    @GetMapping
    public ResponseEntity<List<ResponseKeySoundPlaylistDTO>> getKeySoundPlaylists() {
        return ResponseEntity.ok(playlistKeysoundService.getKeySoundPlaylists());
    }
}
