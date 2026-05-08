package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Cancion;

import java.util.List;

@Repository
public interface CancionRepository extends JpaRepository<Cancion, Long> {

    /**
     * Obtiene las 10 canciones más reproducidas de un artista ordenadas por número de reproducciones
     * Busca tanto por artistas directos de la canción como por propietarios de álbumes de las pistas
     * @param usuarioId ID del artista (usuario)
     * @return Lista de las 10 canciones más reproducidas
     */
    @Query("""
            SELECT DISTINCT c FROM Cancion c
            WHERE (
                c IN (
                    SELECT u.canciones FROM Usuario u WHERE u.id = :usuarioId
                )
                OR c IN (
                    SELECT p.cancion FROM Pista p
                    WHERE p.album.usuario.id = :usuarioId
                )
            )
            ORDER BY SIZE(c.historialReproducciones) DESC LIMIT 10
    """)
    List<Cancion> findTop10CancionesMasReproducidasPorArtista(@Param("usuarioId") Long usuarioId);
}
