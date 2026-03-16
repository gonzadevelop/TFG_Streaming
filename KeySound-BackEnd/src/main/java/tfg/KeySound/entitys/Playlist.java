package tfg.KeySound.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "playlists")
@Getter
@Setter
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

    @Column(length = 500)
    private String descripcion;

    @Column(name = "foto_portada")
    private String fotoPortada;

    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @OneToMany(mappedBy = "playlist")
    private Set<PlaylistPista> playlistPistas = new LinkedHashSet<>();
}
