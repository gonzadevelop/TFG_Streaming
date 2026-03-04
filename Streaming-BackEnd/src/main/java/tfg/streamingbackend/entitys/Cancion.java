package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "canciones")
@Getter
@Setter
public class Cancion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(length = 100)
    private String titulo;

    @Size(max = 255)
    @NotNull
    @Column(name = "archivo_cancion")
    private String archivoCancion;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "cancion_artista",
            joinColumns = @JoinColumn(name = "cancion_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private Set<Usuario> usuarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cancion")
    private Set<CancionProductor> cancionProductores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cancion")
    private Set<LanzamientoCancion> lanzamientoCanciones = new LinkedHashSet<>();
}
