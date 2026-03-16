package tfg.KeySound.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "pistas")
@Getter
@Setter
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "pista")
    private Set<PlaylistPista> playlistPistas = new LinkedHashSet<>();
}
