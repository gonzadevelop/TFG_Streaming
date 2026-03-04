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
public class UsuarioLanzamientoCancionId {

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "lanzamiento_cancion_id")
    private Long lanzamientoCancionId;
}
