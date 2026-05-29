package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Cancion;

import java.time.LocalDateTime;
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
            AND EXISTS (
                SELECT p2.id FROM Pista p2
                WHERE p2.cancion = c
                AND p2.album.esBorrador = false
                AND p2.album.fechaLanzamiento < :ahora
            )
            ORDER BY SIZE(c.historialReproducciones) DESC
    """)
    List<Cancion> findTop10CancionesMasReproducidasPorArtista(@Param("usuarioId") Long usuarioId,
                                                             @Param("ahora") LocalDateTime ahora);
}
