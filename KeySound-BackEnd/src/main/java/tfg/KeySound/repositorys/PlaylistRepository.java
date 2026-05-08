package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Playlist;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("SELECT p FROM Playlist p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Playlist> buscarPorNombre(@Param("q") String q);

    List<Playlist> findByPropietarioId(Long propietarioId);
}

