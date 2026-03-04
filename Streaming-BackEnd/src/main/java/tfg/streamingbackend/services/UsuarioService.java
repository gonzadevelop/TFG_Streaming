package tfg.streamingbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.exception.cancion.CancionNotFoundException;
import tfg.streamingbackend.firebase.FirebaseService;
import tfg.streamingbackend.repositorys.CancionRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final CancionRepository cancionRepository;
    private final FirebaseService firebaseService;

    public String obtenerUrlCancion(Long cancionId) {
        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNotFoundException(cancionId));

        return firebaseService.obtenerUrlArchivo(cancion.getArchivoCancion());
    }
}
