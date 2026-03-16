package tfg.KeySound.entitys;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nombre;

    @OneToMany(mappedBy = "rol")
    private Set<Usuario> usuarios = new LinkedHashSet<>();
}
