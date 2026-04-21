package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tfg.KeySound.mappers.TopMusicalDiarioMapper;
import tfg.KeySound.model.album.RequestAlbumDTO;
import tfg.KeySound.model.album.ResponseAlbumCompletoDTO;
import tfg.KeySound.model.pista.ResponsePistaDTO;
import tfg.KeySound.model.pista.ResponsePistaTopPlaylistDTO;
import tfg.KeySound.model.playlist.ResponseKeySoundPlaylistDTO;
import tfg.KeySound.services.AlbumService;
import tfg.KeySound.services.PlaylistKeysoundService;
import tfg.KeySound.services.RankingService;

import java.util.List;

@RestController
@RequestMapping("/KeySoundPlaylists")
@RequiredArgsConstructor
public class PlaylistKeysoundController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final PlaylistKeysoundService playlistKeysoundService;
    private final RankingService rankingService;

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

    /**
     * Endpoint para obtener el ranking diario de las 30 canciones más escuchadas en la plataforma.
     * Este ranking se actualiza automáticamente cada día a las 00:00, por lo que siempre refleja las tendencias de escucha más recientes.
     * @param fecha (opcional) Si se proporciona una fecha en formato "YYYY-MM-DD", se devuelve el ranking correspondiente a esa fecha. Si no se proporciona, se devuelve el ranking del día anterior.
     * @return {@link ResponseEntity}&lt;{@link List}&lt;{@link ResponsePistaTopPlaylistDTO}&gt;&gt; Devuelve una lista de las 30 canciones más escuchadas con un status 200 (OK)
     * @apiNote {@code GET /api/KeySoundPlaylists/dailyTop30/{fecha?}}
     */
    @GetMapping("/dailyTop30/{fecha}")
    public ResponseEntity<List<ResponsePistaTopPlaylistDTO>> getDailyTop30(
            @PathVariable(required = false) String fecha) {

        return ResponseEntity.ok(rankingService.getDailyTop30(fecha));
    }
}
