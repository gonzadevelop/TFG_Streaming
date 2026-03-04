package tfg.streamingbackend.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.streamingbackend.entitys.Lanzamiento;

@Repository
public interface LanzamientoRepository extends JpaRepository<Lanzamiento, Long> {
}

