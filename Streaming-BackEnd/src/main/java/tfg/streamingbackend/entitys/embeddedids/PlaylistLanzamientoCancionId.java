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
public class PlaylistLanzamientoCancionId {

    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(name = "lanzamiento_cancion_id")
    private Long lanzamientoCancionId;
}
