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
public class UsuarioLanzamientoId {

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "lanzamiento_id")
    private Integer lanzamientoId;
}
