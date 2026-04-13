package tfg.KeySound.repositorys;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.HistorialReproducciones;
import tfg.KeySound.entitys.TopMusicalDiario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface HistorialReproduccionesRepository extends JpaRepository<HistorialReproducciones, Long> {
    @Query("""
            SELECT hr.cancion
            FROM HistorialReproducciones hr
            WHERE hr.fechaReproduccion >= :since
            GROUP BY hr.cancion
            ORDER BY COUNT(hr) DESC
    """)
    List<Cancion> findTopSongsSinceEntities(@Param("since") LocalDateTime since);

    @Query("""
            SELECT COUNT(hr)
            FROM HistorialReproducciones hr
            WHERE hr.cancion.id = :songId AND hr.fechaReproduccion >= :since
    """)
    Long countReproductionsForSongSince(@Param("songId") Long songId, @Param("since") LocalDateTime since);

    @Query(value = """
            SELECT c.*
            FROM canciones c
            JOIN historial_reproducciones hr ON c.id = hr.cancion_id
            WHERE hr.usuario_id = :usuarioId
            GROUP BY c.id
            ORDER BY COUNT(hr.id) DESC
            LIMIT 10
    """, nativeQuery = true)
    List<Cancion> findTop10MostPlayed(Long usuarioId);

    @Query("""
            SELECT COUNT(hr)
            FROM HistorialReproducciones hr
            WHERE hr.usuario.id = :usuarioId AND hr.cancion.id = :cancionId
    """)
    int countReproduccionesByUsuarioAndCancion(@Param("usuarioId") Long usuarioId, @Param("cancionId") Long cancionId);
}
