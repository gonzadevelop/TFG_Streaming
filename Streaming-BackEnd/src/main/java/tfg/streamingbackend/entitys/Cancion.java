package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "canciones")
@Getter
public class Cancion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(length = 100)
    private String titulo;

    @Size(max = 255)
    @NotNull
    @Column(name = "archivo_url")
    private String archivoUrl;

    @ManyToMany(mappedBy = "canciones")
    private Set<Usuario> usuarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cancion")
    private Set<CancionProductor> cancionProductores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cancion")
    private Set<Lanzamiento> lanzamientos = new LinkedHashSet<>();

    public void setLanzamientos(Set<Lanzamiento> lanzamientos) {
        this.lanzamientos = lanzamientos;
    }

    public void setCancionProductores(Set<CancionProductor> cancionProductores) {
        this.cancionProductores = cancionProductores;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
