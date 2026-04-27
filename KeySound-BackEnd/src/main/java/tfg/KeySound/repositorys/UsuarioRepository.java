package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tfg.KeySound.entitys.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByUsernameIgnoreCase(String username);
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
}
