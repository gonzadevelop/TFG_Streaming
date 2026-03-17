package tfg.KeySound.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.TopMusicalDiario;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.TopMusicalDiarioRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio encargado de calcular y actualizar el ranking diario de canciones más escuchadas.
 * Se ejecuta automáticamente cada día a las 00:00.
 */
@Service
public class RankingService {

    /**
     * Inyección de dependencias de los repositorios necesarios para acceder a los datos de canciones y reproducciones.
     */
    private TopMusicalDiarioRepository topMusicalDiarioRepository;
    private HistorialReproduccionesRepository historialReproduccionesRepository;

    /**
     * Metodo programado para ejecutarse diariamente a las 00:00.
     * Calcula el top 30 de canciones más escuchadas en las últimas 24 horas y actualiza la tabla correspondiente.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateDailyTop30() {
        List<Cancion> topSongIds = historialReproduccionesRepository.findTopSongsSinceEntities(LocalDate.now().minusDays(1));

        List<TopMusicalDiario> topEntries = topSongIds.stream()
                .limit(30)
                .map(c -> {
                    TopMusicalDiario entry = new TopMusicalDiario();
                    entry.setCancion(c);
                    entry.setFecha(LocalDate.now());
                    entry.setReproduccionesEnElDia(historialReproduccionesRepository.countReproductionsForSongSince(c.getId(), LocalDate.now().minusDays(1)));
                    entry.setPosicionEnElDia(topSongIds.indexOf(c) + 1);
                    return entry;
                })
                .toList();

        // 2. Limpiar tabla anterior y guardar nueva
        topMusicalDiarioRepository.saveAll(topEntries);
    }

    /**
     * Metodo para obtener el ranking diario actual.
     * @return Lista de TopMusicalDiario con las canciones más escuchadas del día.
     */
    public List<TopMusicalDiario> getDailyTop30() {
        return topMusicalDiarioRepository.findByFecha(LocalDate.now());
    }

    /**
     * Metodo para verificar si el ranking diario está desactualizado (es decir, si el último ranking no corresponde al día actual).
     * @return true si el ranking está desactualizado, false si está actualizado.
     */
    public boolean isRankingOutdated() {
        // Obtener la fecha del último ranking
        LocalDate lastRankingDate = topMusicalDiarioRepository.findTopByOrderByFechaDesc()
                .getFecha();

        return lastRankingDate.isBefore(LocalDate.now());
    }
}
