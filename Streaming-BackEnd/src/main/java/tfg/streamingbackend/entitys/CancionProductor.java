package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import tfg.streamingbackend.entitys.embeddedids.CancionProductorId;

@Entity
@Table(name = "cancion_productores")
@Getter
public class CancionProductor {

    @EmbeddedId
    private CancionProductorId id;

    @MapsId("cancionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;
}
