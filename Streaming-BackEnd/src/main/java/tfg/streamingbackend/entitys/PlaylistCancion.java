package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tfg.streamingbackend.entitys.embeddedids.PlaylistCancionId;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_cancion")
@Getter
@Setter
public class PlaylistCancion {

    @EmbeddedId
    private PlaylistCancionId id;

    @MapsId("playlistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @MapsId("lanzamientoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lanzamiento_id", nullable = false)
    private Lanzamiento lanzamiento;

    @CreationTimestamp
    @Column(name = "fecha_adicion")
    private LocalDateTime fechaAdicion;

}
