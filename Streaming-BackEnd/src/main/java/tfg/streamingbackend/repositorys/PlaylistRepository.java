package tfg.streamingbackend.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.streamingbackend.entitys.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}

