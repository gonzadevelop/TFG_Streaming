package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.PlaylistPista;
import tfg.KeySound.entitys.embeddedids.PlaylistPistaId;

import java.util.Set;

@Repository
public interface PlaylistPistaRepository extends JpaRepository<PlaylistPista, PlaylistPistaId> {
    Set<Long> findPistaIdsByPlaylistId(Long playlistId);
}
