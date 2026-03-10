package tfg.KeySound.entitys;

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

    @OneToMany(mappedBy = "cancion")
    private Set<HistorialReproducciones> historialReproducciones = new LinkedHashSet<>();

    // Duración almacenada en segundos (Integer)
    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;

    // Métodos auxiliares para obtener la duración en minutos y en formato mm:ss
    @Transient
    public Integer getDuracionMinutos() {
        if (this.duracionSegundos == null) return null;
        return this.duracionSegundos / 60;
    }

    @Transient
    public String getDuracionFormato() {
        if (this.duracionSegundos == null) return null;
        int minutos = this.duracionSegundos / 60;
        int segundos = this.duracionSegundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }
}
