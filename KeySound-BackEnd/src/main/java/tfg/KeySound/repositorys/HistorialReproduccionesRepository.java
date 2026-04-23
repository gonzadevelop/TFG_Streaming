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
}
