package tfg.KeySound.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "lanzamientos")
@Getter
@Setter
public class Lanzamiento {

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
    private LocalDate fechaLanzamiento;

    @NotNull
    @Lob
    private String tipo;

    @OneToMany(mappedBy = "lanzamiento")
    private Set<LanzamientoCancion> lanzamientoCanciones = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
