package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String password;

    @Lob
    @Column(name = "tipo_suscripcion")
    private String tipoSuscripcion;

    @Size(max = 255)
    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Lob
    private String biografia;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToMany
    @JoinTable(name = "cancion_artista",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "cancion_id"))
    private Set<Cancion> canciones = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "lanzamiento_id"))
    private Set<Lanzamiento> lanzamientos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<HistorialReproducciones> historialReproducciones = new LinkedHashSet<>();

    @OneToMany(mappedBy = "propietario")
    private Set<Playlist> playlists = new LinkedHashSet<>();

    // COMPROBAR QUE SEGUIDORES Y SEGUIDOS ESTÁ CORRECTAMENTE.

    @ManyToMany
    @JoinTable(name = "seguidores",
            joinColumns = @JoinColumn(name = "seguidor_id"),
            inverseJoinColumns = @JoinColumn(name = "seguido_id"))
    private Set<Usuario> seguidores = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "seguidores",
            joinColumns = @JoinColumn(name = "seguido_id"),
            inverseJoinColumns = @JoinColumn(name = "seguidor_id"))
    private Set<Usuario> seguidos = new LinkedHashSet<>();

}
