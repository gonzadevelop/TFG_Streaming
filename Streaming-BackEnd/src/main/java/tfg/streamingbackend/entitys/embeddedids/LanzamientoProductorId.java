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
public class LanzamientoProductorId {

    @Column(name = "lanzamiento_id")
    private Long lanzamientoId;

    @Column(name = "nombre_productor")
    private String productorNombre;
}
