package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "lanzamientos")
@Getter
public class Lanzamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id")
    private Album album;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancion_id")
    private Cancion cancion;

    @Column(name = "numero_pista")
    private Integer numeroPista;

    @ManyToMany(mappedBy = "lanzamientos")
    private Set<Usuario> usuarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "lanzamiento")
    private Set<HistorialReproducciones> historialReproducciones = new LinkedHashSet<>();

    @OneToMany(mappedBy = "lanzamiento")
    private Set<PlaylistCancion> playlistCancions = new LinkedHashSet<>();
}
