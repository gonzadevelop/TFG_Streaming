package tfg.streamingbackend.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.streamingbackend.entitys.PlaylistLanzamientoCancion;
import tfg.streamingbackend.entitys.embeddedids.PlaylistLanzamientoCancionId;

@Repository
public interface PlaylistLanzamientoCancionRepository extends JpaRepository<PlaylistLanzamientoCancion, PlaylistLanzamientoCancionId> {
    boolean existsByPlaylistIdAndLanzamientoCancionId(Long playlistId, Long lanzamientoCancionId);
}

