package tfg.KeySound.utils;

import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.InvalidFormatFileException;
import tfg.KeySound.repositorys.UsuarioRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilidades auxiliares relacionadas con artistas y validación de archivos.
 */
public final class ArtistaUtils {

    public static void validarFormatoArchivo(String tipo) {
        List<String> formatosPermitidos = Arrays.asList(
                "audio/mpeg",
                "audio/wav",
                "audio/x-wav"
        );

        if (tipo == null || !formatosPermitidos.contains(tipo)) {
            throw new InvalidFormatFileException(tipo);
        }
    }

    public static void validarFormatoImagen(String tipo) {
        List<String> formatosPermitidos = Arrays.asList(
                "image/jpeg",
                "image/png",
                "image/webp"
        );

        if (tipo == null || !formatosPermitidos.contains(tipo)) {
            throw new InvalidFormatFileException(tipo);
        }
    }

    /**
     * Resuelve la lista de colaboradores (usuarios) a partir de una lista de IDs y añade el artista principal.
     * Lanza UsernameNotFoundException si algún ID no existe o no es un artista.
     */
    public static List<Usuario> obtenerColaboradores(List<Long> idArtistas, Usuario artista, UsuarioRepository usuarioRepository) {
        List<Usuario> colaboradores = new ArrayList<>();

        colaboradores.add(artista);

        if (idArtistas != null && !idArtistas.isEmpty()) {
            for (Long idArtista : idArtistas) {
                Usuario colaborador = usuarioRepository.findById(idArtista)
                        .orElseThrow(() -> new UsernameNotFoundException("ID de artista colaborador no encontrado: " + idArtista));
                if (!colaborador.getRol().getNombre().equals("ROLE_ARTISTA")) {
                    throw new UsernameNotFoundException("El usuario con ID " + idArtista + " no es un artista válido.");
                }
                colaboradores.add(colaborador);
            }
        }
        return colaboradores;
    }
}

