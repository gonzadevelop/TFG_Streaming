package tfg.KeySound.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

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
    @Column(name = "archivo_avatar")
    private String archivoAvatar;

    @Lob
    private String biografia;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    // ARTISTAS!!!!
    @ManyToMany
    @JoinTable(name = "cancion_artista",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "cancion_id"))
    private Set<Cancion> canciones = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "pista_id"))
    private Set<Pista> favoritos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<HistorialReproducciones> historialReproducciones = new LinkedHashSet<>();

    @OneToMany(mappedBy = "propietario")
    private Set<Playlist> playlists = new LinkedHashSet<>();

    // ARTISTAS!!!!
    @OneToMany(mappedBy = "usuario")
    private Set<Album> albums = new LinkedHashSet<>();

    // COMPROBAR QUE SEGUIDORES Y SEGUIDOS ESTÁ CORRECTAMENTE.

    @ManyToMany
    @JoinTable(name = "seguidores",
            joinColumns = @JoinColumn(name = "seguidor_id"),
            inverseJoinColumns = @JoinColumn(name = "seguido_id"))
    private Set<Usuario> seguidores = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "seguidores")
    private Set<Usuario> seguidos = new LinkedHashSet<>();


    // metodos de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(rol.getNombre()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
