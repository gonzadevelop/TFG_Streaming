package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.PlaylistPista;
import tfg.KeySound.entitys.embeddedids.PlaylistPistaId;

import java.util.Set;

@Repository
public interface PlaylistPistaRepository extends JpaRepository<PlaylistPista, PlaylistPistaId> {

    @Query("SELECT pp.pista.id FROM PlaylistPista pp WHERE pp.playlist.id = :playlistId")
    Set<Long> findPistaIdsByPlaylistId(@Param("playlistId") Long playlistId);
}
