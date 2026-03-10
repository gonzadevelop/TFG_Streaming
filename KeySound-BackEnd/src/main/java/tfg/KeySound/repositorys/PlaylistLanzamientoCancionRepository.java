package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.PlaylistLanzamientoCancion;
import tfg.KeySound.entitys.embeddedids.PlaylistLanzamientoCancionId;

@Repository
public interface PlaylistLanzamientoCancionRepository extends JpaRepository<PlaylistLanzamientoCancion, PlaylistLanzamientoCancionId> {
    boolean existsByPlaylistIdAndLanzamientoCancionId(Long playlistId, Long lanzamientoCancionId);
}

