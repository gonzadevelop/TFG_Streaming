package tfg.KeySound.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "playlists_keysound")
public class PlaylistKeysound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(length = 100)
    private String nombre;

    private String descripcion;

    @Size(max = 255)
    @Column(name = "url_portada")
    private String urlPortada;
}
