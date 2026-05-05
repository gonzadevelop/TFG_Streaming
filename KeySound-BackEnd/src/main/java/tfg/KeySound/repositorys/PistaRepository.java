package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Pista;

import java.util.List;

public interface PistaRepository extends JpaRepository<Pista, Long> {
    List<Pista> findByCancionTituloContainingIgnoreCase(String titulo);
}

