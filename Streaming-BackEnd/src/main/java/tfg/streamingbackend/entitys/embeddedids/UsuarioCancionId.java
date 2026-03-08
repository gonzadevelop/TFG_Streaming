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
public class UsuarioCancionId {

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "cancion_id")
    private Long cancionId;
}
