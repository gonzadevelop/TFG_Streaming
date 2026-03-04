package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import tfg.streamingbackend.entitys.embeddedids.LanzamientoProductorId;

@Entity
@Table(name = "lanzamiento_productores")
@Getter
public class LanzamientoProductor {

    @EmbeddedId
    private LanzamientoProductorId id;

    @MapsId("lanzamientoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lanzamiento_id", nullable = false)
    private Lanzamiento lanzamiento;
}
