package tfg.KeySound.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.HistorialReproducciones;
import tfg.KeySound.entitys.TopMusicalDiario;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.TopMusicalDiarioRepository;
import tfg.KeySound.services.RankingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Componente que se ejecuta al iniciar la aplicación para verificar si es necesario actualizar el ranking diario.
 */
@Component
@RequiredArgsConstructor
public class StartupRankingUpdater {

    /**
     * Inyección de dependencias del servicio de ranking para poder verificar y actualizar el ranking diario si es necesario.
     */
    private final HistorialReproduccionesRepository historialReproduccionesRepository;
    private final TopMusicalDiarioRepository topMusicalDiarioRepository;

    /**
     * Metodo que se ejecuta al iniciar la aplicación. Verifica si el ranking diario está desactualizado y, si es así, lo actualiza.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkTopOnStartup() {
        if (isRankingOutdated()) updateDailyTop30();
    }

    /**
     * Metodo programado para ejecutarse diariamente a las 00:00.
     * Calcula el top 30 de canciones más escuchadas en las últimas 24 horas y actualiza la tabla correspondiente.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateDailyTop30() {
        List<Cancion> topSongIds = historialReproduccionesRepository.findTopSongsSinceEntities(LocalDateTime.now().minusDays(1));

        List<TopMusicalDiario> topEntries = topSongIds.stream()
                .limit(30)
                .map(c -> {
                    TopMusicalDiario entry = new TopMusicalDiario();
                    entry.setCancion(c);
                    entry.setFecha(LocalDate.now());

                    Long countLong = historialReproduccionesRepository.countReproductionsForSongSince(c.getId(), LocalDateTime.now().minusDays(1));
                    int reproducciones = 0;
                    if (countLong != null) {
                        if (countLong > Integer.MAX_VALUE) reproducciones = Integer.MAX_VALUE;
                        else reproducciones = countLong.intValue();
                    }
                    entry.setReproduccionesEnElDia(reproducciones);

                    entry.setPosicionEnElDia(topSongIds.indexOf(c) + 1);
                    return entry;
                })
                .toList();

        // Guardar el nuevo ranking diario en la base de datos
        topMusicalDiarioRepository.saveAll(topEntries);
    }

    /**
     * Metodo para verificar si el ranking diario está desactualizado (es decir, si el último ranking no corresponde al día actual).
     * @return true si el ranking está desactualizado, false si está actualizado.
     */
    public boolean isRankingOutdated() {
        // Obtener la fecha del último ranking
        LocalDate lastRankingDate = topMusicalDiarioRepository
                .findTopByOrderByFechaDesc()
                .getFecha();

        // comprobar si la fecha del último ranking es anterior a la fecha actual
        return lastRankingDate == null || lastRankingDate.isBefore(LocalDate.now());
    }

}
