package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.mappers.PlaylistKeysoundMapper;
import tfg.KeySound.model.playlist.ResponseKeySoundPlaylistDTO;
import tfg.KeySound.repositorys.*;
import tfg.KeySound.services.external.FirebaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PlaylistKeysoundService {

    /**
     * Inyecciones por constructor
     */
    private final FirebaseService firebaseService;

    private final PlaylistKeysoundRepository playlistKeysoundRepository;

    private final PlaylistKeysoundMapper playlistKeysoundMapper;

    /**
     * Metodos llamados por endpoints
     */
    public List<ResponseKeySoundPlaylistDTO> getKeySoundPlaylists() {
        return playlistKeysoundRepository
                .findAll()
                .stream()
                .map( p ->
                     playlistKeysoundMapper
                            .toDto(
                                    p,
                                    "/KeySoundPlaylists/" + p.getNombre(),
                                    firebaseService.obtenerUrlArchivoImagen(p.getUrlPortada(), "")
                    )
                )
                .toList();
    }
}
