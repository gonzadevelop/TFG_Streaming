package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}

