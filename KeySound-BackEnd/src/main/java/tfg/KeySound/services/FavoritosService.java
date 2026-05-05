package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.model.favoritos.ResponseFavoritosDTO;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.repositorys.PistaRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritosService {

    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;
    private final FirebaseService firebaseService;
    private final PistaMapper pistaMapper;

    public void añadirFavorito(Long pistaId, String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Pista pista = pistaRepository.findById(pistaId)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada"));
        usuario.getFavoritos().add(pista);
        usuarioRepository.save(usuario);
    }

    public void eliminarFavorito(Long pistaId, String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.getFavoritos().removeIf(p -> p.getId().equals(pistaId));
        usuarioRepository.save(usuario);
    }

    public ResponseFavoritosDTO obtenerFavoritos(String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getFavoritos() == null || usuario.getFavoritos().isEmpty()) {
            return new ResponseFavoritosDTO(Collections.emptyList());
        }

        List<ResponsePistaPlaylistDTO> pistas = usuario.getFavoritos().stream()
                .map(pista -> pistaMapper.pistaToPlaylistDto(
                        pista,
                        firebaseService.obtenerUrlArchivoImagen(pista.getAlbum().getArchivoPortada(), ""),
                        firebaseService.obtenerUrlArchivoAudio(pista.getCancion().getArchivoCancion()),
                        pista.getCancion().getUsuarios().stream()
                                .map(u -> u.getUsername())
                                .toList()
                ))
                .toList();

        return new ResponseFavoritosDTO(pistas);
    }
}
