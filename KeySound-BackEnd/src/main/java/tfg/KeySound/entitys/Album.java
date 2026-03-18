package tfg.KeySound.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "albums")
@Getter
@Setter
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @Size(max = 255)
    @Column(name = "archivo_portada")
    private String archivoPortada;

    @Column(name = "fecha_lanzamiento")
    private LocalDateTime fechaLanzamiento;

    @NotNull
    @Lob
    private String tipo;

    @Column(name = "es_borrador")
    private Boolean esBorrador;

    @OneToMany(mappedBy = "album")
    private Set<Pista> pistas = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
