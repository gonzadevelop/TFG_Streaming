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
    List<Album> findByEsBorradorFalseAndFechaLanzamientoAfterAndFechaLanzamientoBeforeOrderByFechaLanzamientoDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtiene los álbumes publicados de un artista ordenados por fecha de lanzamiento descendente
     * @param usuarioId ID del artista (usuario)
     * @return Lista de álbumes que no son borradores y con fecha de lanzamiento anterior a ahora
     */
    @Query("""
            SELECT a FROM Album a
            WHERE a.usuario.id = :usuarioId
            AND a.esBorrador = false
            AND a.fechaLanzamiento < :ahora
            ORDER BY a.fechaLanzamiento DESC
            """)
    List<Album> findAlbumsPublicadosPorArtista(@Param("usuarioId") Long usuarioId,
                                               @Param("ahora") LocalDateTime ahora);

    @Query("SELECT a FROM Album a WHERE a.esBorrador = false AND LOWER(a.titulo) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Album> buscarPorTitulo(@Param("q") String q);

    /**
     * Obtiene todos los álbumes de un artista ordenados por fecha de lanzamiento descendente
     * @param usuarioId ID del artista (usuario)
     * @return Lista de todos los álbumes del artista ordenados por fecha de lanzamiento descendente
     */
    @Query("""
            SELECT a FROM Album a
            WHERE a.usuario.id = :usuarioId
            ORDER BY a.fechaLanzamiento DESC
            """)
    List<Album> findAllByArtistaOrderByFechaLanzamientoDesc(@Param("usuarioId") Long usuarioId);
}
