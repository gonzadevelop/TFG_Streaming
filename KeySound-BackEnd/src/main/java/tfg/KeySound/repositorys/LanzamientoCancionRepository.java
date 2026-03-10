package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.LanzamientoCancion;

import java.util.Optional;

@Repository
public interface LanzamientoCancionRepository extends JpaRepository<LanzamientoCancion, Long> {
    Optional<LanzamientoCancion> findByLanzamientoIdAndCancionId(Long idLanzamiento, Long idCancion);
}

