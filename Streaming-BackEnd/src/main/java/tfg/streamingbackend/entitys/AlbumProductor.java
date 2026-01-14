package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import tfg.streamingbackend.entitys.embeddedids.AlbumProductorId;

@Entity
@Table(name = "album_productores")
@Getter
public class AlbumProductor {

    @EmbeddedId
    private AlbumProductorId id;

    @MapsId("albumId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;
}
