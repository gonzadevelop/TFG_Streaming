package tfg.streamingbackend.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.streamingbackend.entitys.Cancion;

@Repository
public interface CancionRepository extends JpaRepository<Cancion, Long> {
}

