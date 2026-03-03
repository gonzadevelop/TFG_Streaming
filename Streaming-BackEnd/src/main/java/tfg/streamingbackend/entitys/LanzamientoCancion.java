package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "lanzamiento_canciones")
@Getter
public class LanzamientoCancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lanzamiento_id")
    private Lanzamiento lanzamiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancion_id")
    private Cancion cancion;

    @Column(name = "numero_pista")
    private Integer numeroPista;

    @ManyToMany(mappedBy = "lanzamientoCanciones")
    private Set<Usuario> usuarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "lanzamientoCancion")
    private Set<HistorialReproducciones> historialReproducciones = new LinkedHashSet<>();

    @OneToMany(mappedBy = "lanzamientoCancion")
    private Set<PlaylistLanzamientoCancion> playlistLanzamientoCanciones = new LinkedHashSet<>();
}
