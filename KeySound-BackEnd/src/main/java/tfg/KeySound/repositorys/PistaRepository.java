package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Pista;

import java.util.List;

public interface PistaRepository extends JpaRepository<Pista, Long> {
    List<Pista> findByCancionTituloContainingIgnoreCase(String titulo);
    List<Pista> findByAlbumId(Long albumId);

    boolean existsByCancionIdAndAlbumUsuarioId(Long cancionId, Long usuarioId);

    @Query("""
            SELECT p FROM Pista p
            WHERE p.album.usuario.id = :artistaId
              AND LOWER(p.cancion.titulo) LIKE LOWER(CONCAT('%', :q, '%'))
            ORDER BY p.cancion.titulo ASC
    """)
    List<Pista> buscarCancionesDeArtista(@Param("artistaId") Long artistaId, @Param("q") String q);
}
