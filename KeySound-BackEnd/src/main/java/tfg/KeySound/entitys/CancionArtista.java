package tfg.KeySound.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import tfg.KeySound.entitys.embeddedids.CancionArtistaId;

@Entity
@Table(name = "cancion_artista")
@Getter
public class CancionArtista {

    @EmbeddedId
    private CancionArtistaId id;

    @MapsId("cancionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
