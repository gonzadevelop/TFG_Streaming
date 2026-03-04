package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tfg.streamingbackend.entitys.embeddedids.PlaylistLanzamientoCancionId;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_lanzamiento_cancion")
@Getter
@Setter
public class PlaylistLanzamientoCancion {

    @EmbeddedId
    private PlaylistLanzamientoCancionId id;

    @MapsId("playlistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @MapsId("lanzamientoCancionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lanzamiento_cancion_id", nullable = false)
    private LanzamientoCancion lanzamientoCancion;

    @CreationTimestamp
    @Column(name = "fecha_adicion")
    private LocalDateTime fechaAdicion;

}
