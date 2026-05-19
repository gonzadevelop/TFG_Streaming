package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tfg.KeySound.model.cancion.TopMusicalDiarioDTO;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.HistorialReproducciones;
import tfg.KeySound.entitys.TopMusicalDiario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface HistorialReproduccionesRepository extends JpaRepository<HistorialReproducciones, Long> {

    @Query(value = """
            SELECT c.*
            FROM canciones c
            JOIN historial_reproducciones hr ON c.id = hr.cancion_id
            WHERE hr.usuario_id = :usuarioId
            GROUP BY c.id
            ORDER BY COUNT(hr.id) DESC
            LIMIT 10
    """, nativeQuery = true)
    List<Cancion> findTop10MostPlayed(Long usuarioId);

    @Query("""
            SELECT COUNT(hr)
            FROM HistorialReproducciones hr
            WHERE hr.usuario.id = :usuarioId AND hr.cancion.id = :cancionId
    """)
    int countReproduccionesByUsuarioAndCancion(@Param("usuarioId") Long usuarioId, @Param("cancionId") Long cancionId);

    @Query("""
            SELECT new tfg.KeySound.model.cancion.TopMusicalDiarioDTO(COUNT(hr), hr.cancion)
            FROM HistorialReproducciones hr
            WHERE FUNCTION('DATE', hr.fechaReproduccion) = :date
            GROUP BY hr.cancion.id, hr.cancion
            ORDER BY COUNT(hr) DESC
    """)
    List<TopMusicalDiarioDTO> findTopSongsByDateAsDTO(@Param("date") LocalDate date);

    default List<TopMusicalDiario> findTop30ByFecha(LocalDate fecha) {
        List<TopMusicalDiarioDTO> dtos = findTopSongsByDateAsDTO(fecha);
        List<TopMusicalDiario> resultado = new ArrayList<>();
        int posicion = 1;
        int limite = Math.min(30, dtos.size());
        for (int i = 0; i < limite; i++) {
            TopMusicalDiarioDTO dto = dtos.get(i);
            TopMusicalDiario t = new TopMusicalDiario();
            t.setFecha(dto.getFecha());
            t.setReproduccionesEnElDia(dto.getReproduccionesEnElDia() != null ? dto.getReproduccionesEnElDia().intValue() : 0);
            t.setCancion(dto.getCancion());
            t.setFecha(fecha);
            t.setPosicionEnElDia(posicion);
            resultado.add(t);
            posicion++;
        }
        return resultado;
    }

    // ---- Estadísticas mensuales ----

    @Query(value = """
            SELECT COALESCE(SUM(c.duracion_segundos), 0)
            FROM historial_reproducciones hr
            JOIN canciones c ON c.id = hr.cancion_id
            WHERE hr.usuario_id = :usuarioId
              AND hr.fecha_reproduccion >= :desde
              AND hr.fecha_reproduccion < :hasta
    """, nativeQuery = true)
    Long sumSegundosEscuchadosMes(@Param("usuarioId") Long usuarioId,
                                   @Param("desde") LocalDateTime desde,
                                   @Param("hasta") LocalDateTime hasta);

    @Query(value = """
            SELECT COUNT(hr.id)
            FROM historial_reproducciones hr
            WHERE hr.usuario_id = :usuarioId
              AND hr.fecha_reproduccion >= :desde
              AND hr.fecha_reproduccion < :hasta
    """, nativeQuery = true)
    Long countReproduccionesMes(@Param("usuarioId") Long usuarioId,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);

    @Query(value = """
            SELECT c.id AS cancionId,
                   c.titulo AS titulo,
                   c.duracion_segundos AS duracionSegundos,
                   COUNT(hr.id) AS reproducciones,
                   c.archivo_cancion AS archivoCancion,
                   (SELECT al2.archivo_portada
                    FROM pistas p2
                    JOIN albums al2 ON al2.id = p2.album_id
                    WHERE p2.cancion_id = c.id
                    LIMIT 1) AS archivoPortada,
                   (SELECT u2.username
                    FROM usuarios u2
                    WHERE u2.id IN (
                        SELECT a2.usuario_id FROM pistas p3
                        JOIN albums a2 ON a2.id = p3.album_id
                        WHERE p3.cancion_id = c.id
                        UNION
                        SELECT ca2.usuario_id FROM cancion_artista ca2
                        WHERE ca2.cancion_id = c.id
                    )
                    LIMIT 1) AS artista
            FROM historial_reproducciones hr
            JOIN canciones c ON c.id = hr.cancion_id
            WHERE hr.usuario_id = :usuarioId
              AND hr.fecha_reproduccion >= :desde
              AND hr.fecha_reproduccion < :hasta
            GROUP BY c.id, c.titulo, c.duracion_segundos, c.archivo_cancion
            ORDER BY reproducciones DESC
            LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findTop5CancionesMes(@Param("usuarioId") Long usuarioId,
                                         @Param("desde") LocalDateTime desde,
                                         @Param("hasta") LocalDateTime hasta);

    @Query(value = """
            SELECT u.id AS artistaId, u.username AS nombre, COUNT(hr.id) AS reproducciones, u.username AS username
            FROM historial_reproducciones hr
            JOIN cancion_artista ca ON ca.cancion_id = hr.cancion_id
            JOIN usuarios u ON u.id = ca.usuario_id
            WHERE hr.usuario_id = :usuarioId
              AND hr.fecha_reproduccion >= :desde
              AND hr.fecha_reproduccion < :hasta
            GROUP BY u.id, u.username
            ORDER BY reproducciones DESC
            LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findTop5ArtistasMes(@Param("usuarioId") Long usuarioId,
                                        @Param("desde") LocalDateTime desde,
                                        @Param("hasta") LocalDateTime hasta);

    @Query(value = """
            SELECT al.id AS albumId,
                   al.titulo AS titulo,
                   al.archivo_portada AS archivoPortada,
                   COUNT(hr.id) AS reproducciones,
                   (SELECT u2.username FROM usuarios u2 WHERE u2.id = al.usuario_id LIMIT 1) AS artista
            FROM historial_reproducciones hr
            JOIN pistas p ON p.cancion_id = hr.cancion_id
            JOIN albums al ON al.id = p.album_id
            WHERE hr.usuario_id = :usuarioId
              AND hr.fecha_reproduccion >= :desde
              AND hr.fecha_reproduccion < :hasta
            GROUP BY al.id, al.titulo, al.archivo_portada, al.usuario_id
            ORDER BY reproducciones DESC
            LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findTop5AlbumesMes(@Param("usuarioId") Long usuarioId,
                                       @Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta);
}
