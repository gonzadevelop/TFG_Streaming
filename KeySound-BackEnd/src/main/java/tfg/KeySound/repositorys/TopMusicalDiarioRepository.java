package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.TopMusicalDiario;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TopMusicalDiarioRepository extends JpaRepository<TopMusicalDiario, Long> {
    TopMusicalDiario findTopByOrderByFechaDesc();
    List<TopMusicalDiario> findByFecha(LocalDate fecha);

    /**
     * Metodo para encontrar las pistas del ranking diario de una fecha concreta
     */
    @Query("""
        SELECT p
        FROM TopMusicalDiario t
            JOIN t.cancion c
            JOIN c.pistas p
        WHERE t.fecha = :fecha
        ORDER BY t.posicionEnElDia ASC
    """)
    List<Pista> findPistasByFecha(LocalDate fecha);
}
