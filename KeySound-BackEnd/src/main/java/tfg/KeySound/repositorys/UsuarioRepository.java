package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.KeySound.entitys.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByUsernameIgnoreCase(String username);
}
