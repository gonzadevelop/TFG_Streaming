package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Lanzamiento;

@Repository
public interface LanzamientoRepository extends JpaRepository<Lanzamiento, Long> {
}

