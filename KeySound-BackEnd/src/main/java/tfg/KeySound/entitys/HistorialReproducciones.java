package tfg.KeySound.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tfg.KeySound.entitys.embeddedids.UsuarioCancionId;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_reproducciones")
@Getter
@Setter
public class HistorialReproducciones {

    @EmbeddedId
    private UsuarioCancionId id;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @MapsId("cancionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;

    @CreationTimestamp
    @Column(name = "fecha_reproduccion")
    private LocalDateTime fechaReproduccion;

}
