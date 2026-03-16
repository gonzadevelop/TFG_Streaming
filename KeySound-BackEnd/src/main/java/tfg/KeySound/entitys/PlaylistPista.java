package tfg.KeySound.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import tfg.KeySound.entitys.embeddedids.PlaylistPistaId;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_pistas")
@Getter
@Setter
public class PlaylistPista {

    @EmbeddedId
    private PlaylistPistaId id;

    @MapsId("playlistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @MapsId("pistaId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pista_id", nullable = false)
    private Pista pista;

    @CreationTimestamp
    @Column(name = "fecha_adicion")
    private LocalDateTime fechaAdicion;
}
