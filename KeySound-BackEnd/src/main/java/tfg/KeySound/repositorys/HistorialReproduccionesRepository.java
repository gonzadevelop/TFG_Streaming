package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.HistorialReproducciones;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistorialReproduccionesRepository extends JpaRepository<HistorialReproducciones, Long> {
    @Query("""
            SELECT hr.cancion
            FROM HistorialReproducciones hr
            WHERE hr.fechaReproduccion >= :since
            GROUP BY hr.cancion
            ORDER BY COUNT(hr) DESC""")
    List<Cancion> findTopSongsSinceEntities(@Param("since") LocalDate since);

    @Query("""
            SELECT COUNT(hr)
            FROM HistorialReproducciones hr
            WHERE hr.cancion.id = :songId AND hr.fechaReproduccion >= :since
    """)
    Integer countReproductionsForSongSince(Long id, LocalDate localDate);
}
