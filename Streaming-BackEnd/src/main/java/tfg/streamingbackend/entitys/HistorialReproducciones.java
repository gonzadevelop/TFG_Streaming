package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tfg.streamingbackend.entitys.embeddedids.UsuarioLanzamientoCancionId;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_reproducciones")
@Getter
@Setter
public class HistorialReproducciones {

    @EmbeddedId
    private UsuarioLanzamientoCancionId id;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @MapsId("lanzamientoCancionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lanzamiento_cancion_id", nullable = false)
    private LanzamientoCancion lanzamientoCancion;

    @CreationTimestamp
    @Column(name = "fecha_reproduccion")
    private LocalDateTime fechaReproduccion;

}
