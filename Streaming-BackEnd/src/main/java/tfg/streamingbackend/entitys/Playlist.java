package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "playlists")
@Getter
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "es_publica")
    private Boolean esPublica;

    @OneToMany(mappedBy = "playlist")
    private Set<PlaylistLanzamientoCancion> playlistLanzamientoCanciones = new LinkedHashSet<>();
}
