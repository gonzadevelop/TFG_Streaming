package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import tfg.streamingbackend.entitys.embeddedids.PlaylistCancionId;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_cancion")
@Getter
public class PlaylistCancion {

    @EmbeddedId
    private PlaylistCancionId id;

    @MapsId("playlistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @MapsId("cancionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lanzamiento_id", nullable = false)
    private Lanzamiento lanzamiento;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_adicion")
    private LocalDateTime fechaAdicion;

}
