package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.PlaylistKeysound;

@Repository
public interface PlaylistKeysoundRepository extends JpaRepository<PlaylistKeysound, Long> {
}

