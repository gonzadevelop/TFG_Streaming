package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "albumes")
@Getter
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @Size(max = 255)
    @Column(name = "portada_url")
    private String portadaUrl;

    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    @NotNull
    @Lob
    private String tipo;

    @OneToMany(mappedBy = "album")
    private Set<AlbumProductor> albumProductores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "album")
    private Set<Lanzamiento> lanzamientos = new LinkedHashSet<>();
}
