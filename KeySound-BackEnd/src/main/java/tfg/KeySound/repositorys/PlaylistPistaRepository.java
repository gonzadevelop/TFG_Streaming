package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.PlaylistPista;
import tfg.KeySound.entitys.embeddedids.PlaylistPistaId;

@Repository
public interface PlaylistPistaRepository extends JpaRepository<PlaylistPista, PlaylistPistaId> {
    boolean existsByPlaylistIdAndPistaId(Long playlistId, Long pistaId);
}
