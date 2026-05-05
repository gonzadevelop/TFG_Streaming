package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Album;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByEsBorradorFalseAndFechaLanzamientoAfterOrderByFechaLanzamientoAsc(LocalDateTime fecha);

    @Query("SELECT a FROM Album a WHERE a.esBorrador = false AND LOWER(a.titulo) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Album> buscarPorTitulo(@Param("q") String q);
}

