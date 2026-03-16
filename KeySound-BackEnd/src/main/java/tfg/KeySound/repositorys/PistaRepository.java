package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Pista;

import java.util.Optional;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {
}
