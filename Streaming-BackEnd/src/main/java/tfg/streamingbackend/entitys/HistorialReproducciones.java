package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tfg.streamingbackend.entitys.embeddedids.UsuarioLanzamientoId;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_reproducciones")
@Getter
@Setter
public class HistorialReproducciones {

    @EmbeddedId
    private UsuarioLanzamientoId id;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @MapsId("lanzamientoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lanzamiento_id", nullable = false)
    private Lanzamiento lanzamiento;

    @CreationTimestamp
    @Column(name = "fecha_reproduccion")
    private LocalDateTime fechaReproduccion;

}
