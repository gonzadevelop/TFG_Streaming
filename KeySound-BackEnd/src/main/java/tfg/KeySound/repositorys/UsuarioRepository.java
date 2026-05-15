package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tfg.KeySound.entitys.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByUsernameIgnoreCase(String username);

    /**
     * Busca usuarios por coincidencia parcial de username (case-insensitive) y rol de artista (id = 3)
     * @param q Cadena de búsqueda para el username
     * @return Lista de usuarios que coinciden con la búsqueda y son artistas
     */
    @Query("""
            SELECT u
            FROM Usuario u
            WHERE LOWER(u.username)
            LIKE LOWER(CONCAT('%', :q, '%'))
            AND u.rol.id = 3
    """)
    List<Usuario> buscarArtistaPorUsername(@Param("q") String q);

    /**
     * Cuenta cuántas canciones de un artista tiene un usuario en favoritos. Se consideran canciones del artista aquellas que son propiedad de un álbum del artista o que tienen al artista como colaborador.
     * @param usuarioId ID del usuario
     * @param artistaId ID del artista
     * @return Número de canciones del artista que el usuario tiene en favoritos
     */
    @Query("""
            SELECT COUNT(DISTINCT p) FROM Pista p
            JOIN p.cancion c
            JOIN p.album alb
            LEFT JOIN c.usuarios artistaCancion
            WHERE p IN (
                SELECT fav FROM Usuario usr
                JOIN usr.favoritos fav
                WHERE usr.id = :usuarioId
            )
            AND (
                artistaCancion.id = :artistaId
                OR alb.usuario.id = :artistaId
            )
    """)
    int countFavoritosByUsuarioAndArtista(@Param("usuarioId") Long usuarioId,
                                           @Param("artistaId") Long artistaId);
    
    /**
     * Obtiene los artistas que sigue un usuario (usuarios con rol ROLE_ARTISTA)
     * @param usuarioId ID del usuario
     * @return Lista de artistas que el usuario sigue
     */
    @Query("""
            SELECT u FROM Usuario u
            WHERE u IN (
                SELECT s FROM Usuario usr
                JOIN usr.seguidos s
                WHERE usr.id = :usuarioId
            )
            AND u.rol.id = 3
            """)
    List<Usuario> findArtistasQueSigue(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene los artistas de una canción (propietario del álbum + colaboradores)
     * @param cancionId ID de la canción
     * @return Lista de nombres de usuario de los artistas
     */
    @Query(value = """
            SELECT DISTINCT u.username
            FROM usuarios u
            WHERE u.id IN (
                -- Propietario del álbum
                SELECT a.usuario_id
                FROM pistas p
                INNER JOIN albums a ON p.album_id = a.id
                WHERE p.cancion_id = :cancionId
                UNION
                -- Artistas colaboradores
                SELECT usuario_id
                FROM cancion_artista
                WHERE cancion_id = :cancionId
            )
            ORDER BY u.username
    """, nativeQuery = true)
    List<String> findArtistasDeCancion(@Param("cancionId") Long cancionId);
}
