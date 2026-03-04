package tfg.streamingbackend.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.streamingbackend.entitys.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);
}
