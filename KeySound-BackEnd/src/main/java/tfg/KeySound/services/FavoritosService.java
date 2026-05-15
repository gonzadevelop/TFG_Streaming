package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.pista.PistaNotFoundException;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.model.pista.ResponsePistaAlbumDTO;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.repositorys.PistaRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.JwtService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritosService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;

    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;

    private final PistaMapper pistaMapper;

    public void anadirFavorito(Long pistaId, String token) {
        String username = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Pista pista = pistaRepository.findById(pistaId)
                .orElseThrow(() -> new PistaNotFoundException(pistaId));

        usuario.getFavoritos().add(pista);
        usuarioRepository.save(usuario);
    }

    public void eliminarFavorito(Long pistaId, String token) {
        String username = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        usuario.getFavoritos().removeIf(p -> p.getId().equals(pistaId));
        usuarioRepository.save(usuario);
    }

    public List<ResponsePistaPlaylistDTO> obtenerFavoritos(String token) {
        String username = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (usuario.getFavoritos() == null || usuario.getFavoritos().isEmpty())
            return List.of();

        return pistaMapper.pistasToPlaylistDtos(usuario.getFavoritos().stream().toList());
    }

    public List<ResponsePistaAlbumDTO> obtenerFavoritosAlbum(String token) {
        String username = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (usuario.getFavoritos() == null || usuario.getFavoritos().isEmpty())
            return List.of();

        return pistaMapper.pistasToAlbumDtos(usuario.getFavoritos().stream().toList());
    }
}
