package tfg.KeySound.entitys.embeddedids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PlaylistPistaId {

    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(name = "pista_id")
    private Long pistaId;
}
