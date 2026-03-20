package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.TopMusicalDiario;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TopMusicalDiarioRepository extends JpaRepository<TopMusicalDiario, Long> {
    TopMusicalDiario findTopByOrderByFechaDesc();
    List<TopMusicalDiario> findByFecha(LocalDate fecha);
}

