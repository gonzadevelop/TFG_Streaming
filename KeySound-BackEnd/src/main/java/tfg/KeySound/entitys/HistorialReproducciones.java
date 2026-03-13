package tfg.KeySound.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_reproducciones")
@Getter
@Setter
public class HistorialReproducciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;

    @CreationTimestamp
    @Column(name = "fecha_reproduccion")
    private LocalDateTime fechaReproduccion;

}
