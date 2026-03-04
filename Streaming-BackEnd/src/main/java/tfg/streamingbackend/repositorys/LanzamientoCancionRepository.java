package tfg.streamingbackend.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.streamingbackend.entitys.LanzamientoCancion;

@Repository
public interface LanzamientoCancionRepository extends JpaRepository<LanzamientoCancion, Long> {
}

