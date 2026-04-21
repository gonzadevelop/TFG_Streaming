package tfg.KeySound.model.cancion;

import lombok.Data;
import lombok.NoArgsConstructor;
import tfg.KeySound.entitys.Cancion;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TopMusicalDiarioDTO {
    private LocalDate fecha;
    private Long reproduccionesEnElDia;
    private Cancion cancion;
    private Integer posicionEnElDia;

    public TopMusicalDiarioDTO(Long reproduccionesEnElDia, Cancion cancion) {
        this.reproduccionesEnElDia = reproduccionesEnElDia;
        this.cancion = cancion;
    }
}
