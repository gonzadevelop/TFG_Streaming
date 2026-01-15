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
public class PlaylistCancionId {

    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(name = "cancion_id")
    private Long cancionId;
}
