package tfg.KeySound.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "top_musical_diario")
@Getter
@Setter
public class TopMusicalDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "posicion_en_el_dia")
    private Integer posicionEnElDia;

    @NotNull
    private LocalDate fecha;

    @Column(name = "reproducciones_en_el_dia")
    private Integer reproduccionesEnElDia;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cancion cancion;
}
