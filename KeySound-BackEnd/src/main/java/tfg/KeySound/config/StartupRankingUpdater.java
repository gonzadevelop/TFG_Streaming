package tfg.KeySound.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tfg.KeySound.services.RankingService;

/**
 * Componente que se ejecuta al iniciar la aplicación para verificar si es necesario actualizar el ranking diario.
 */
@Component
@RequiredArgsConstructor
public class StartupRankingUpdater {

    /**
     * Inyección de dependencias del servicio de ranking para poder verificar y actualizar el ranking diario si es necesario.
     */
    private RankingService rankingService;

    /**
     * Metodo que se ejecuta al iniciar la aplicación. Verifica si el ranking diario está desactualizado y, si es así, lo actualiza.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkTopOnStartup() {
        if (rankingService.isRankingOutdated()) rankingService.updateDailyTop30();
    }
}
