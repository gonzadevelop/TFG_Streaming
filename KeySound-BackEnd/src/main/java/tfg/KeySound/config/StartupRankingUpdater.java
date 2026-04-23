package tfg.KeySound.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tfg.KeySound.repositorys.TopMusicalDiarioRepository;
import tfg.KeySound.services.RankingService;

import java.time.LocalDate;

/**
 * Componente que se ejecuta al iniciar la aplicación para verificar si es necesario actualizar el ranking diario.
 */
@Component
@RequiredArgsConstructor
public class StartupRankingUpdater {

    /**
     * Inyección de dependencias del servicio de ranking para poder verificar y actualizar el ranking diario si es necesario.
     */
    private final RankingService rankingService;

    /**
     * Metodo programado para ejecutarse diariamente a las 00:00 y actualizar el ranking diario de canciones más escuchadas.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateDailyTop30() {
        rankingService.updateDailyTop30();
    }

    /**
     * Metodo que se ejecuta al iniciar la aplicación. Verifica si el ranking diario está desactualizado y, si es así, lo actualiza.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkTopOnStartup() {
        if (false)
            rankingService.updateDailyTop30();
    }
}
