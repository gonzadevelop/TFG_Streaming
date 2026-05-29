package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.*;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.mappers.TopMusicalDiarioMapper;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistCompletaDTO;
import tfg.KeySound.repositorys.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {


    /**
     * Inyecciones por constructor
     */
    private final TopMusicalDiarioRepository topMusicalDiarioRepository;
    private final HistorialReproduccionesRepository historialReproduccionesRepository;
    private final PlaylistRepository playlistRepository;

    private final TopMusicalDiarioMapper topMusicalDiarioMapper;
    private final PistaMapper pistaMapper;

    /**
     * Metodo para obtener el ranking diario actual.
     * Si no se proporciona una fecha, se devuelve el ranking del día anterior (ya que el ranking se actualiza a las 00:00).
     */
    public ResponsePlaylistCompletaDTO getDailyTop30(String fecha) {
        LocalDate fechaConsulta = fecha == null || fecha.isEmpty()
                ? LocalDate.now().minusDays(1)
                : LocalDate.parse(fecha);

        Playlist entity = playlistRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("No se encontró la playlist de ranking diario"));

        // Obtener las pistas del ranking diario para la fecha dada y mapear a ResponsePistaPlaylistDTO
        List<TopMusicalDiario> pistas = topMusicalDiarioRepository.findByFecha(fechaConsulta);
        pistas.sort(Comparator.comparingLong((TopMusicalDiario ranking) ->
                        ranking.getReproduccionesEnElDia() == null ? 0L : ranking.getReproduccionesEnElDia())
                .reversed());

        List<ResponsePistaPlaylistDTO> pistasDTO = pistaMapper.topMusicalDiarioToPlaylistDtos(pistas);

        return topMusicalDiarioMapper.toDto(entity, pistasDTO);
    }

    /**
     * Metodo que calcula el top 30 de canciones más escuchadas en las últimas 24 horas y actualiza la tabla correspondiente.
     */
    public void updateDailyTop30() {
        // Obtener el top 30 de canciones más escuchadas en las últimas 24 horas
        List<TopMusicalDiario> topMusicalDiario = historialReproduccionesRepository.findTop30ByFecha(LocalDate.now().minusDays(1));

        // Guardar el ranking del dia de hoy en la BD
        topMusicalDiarioRepository.saveAll(topMusicalDiario);
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
        return !lastRankingDate.isEqual(LocalDate.now().minusDays(1));
    }
}
