package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.TopMusicalDiario;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.TopMusicalDiarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de calcular y actualizar el ranking diario de canciones más escuchadas.
 * Se ejecuta automáticamente cada día a las 00:00.
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    /**
     * Inyección de dependencias de los repositorios necesarios para acceder a los datos de canciones y reproducciones.
     */
    private final TopMusicalDiarioRepository topMusicalDiarioRepository;
    private final HistorialReproduccionesRepository historialReproduccionesRepository;


    /**
     * Metodo para obtener el ranking diario actual.
     * @return Lista de TopMusicalDiario con las canciones más escuchadas del día.
     */
    public List<TopMusicalDiario> getDailyTop30() {
        return topMusicalDiarioRepository.findByFecha(LocalDate.now().minusDays(1));
    }
}
