package tfg.streamingbackend.entitys.embeddedids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CancionProductorId {

    @Column(name = "cancion_id")
    private Integer cancionId;

    @Column(name = "usuario_id")
    private Integer usuarioId;
}
