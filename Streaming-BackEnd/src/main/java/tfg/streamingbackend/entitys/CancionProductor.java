package tfg.streamingbackend.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cancion_productores")
@Getter
@Setter
public class CancionProductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;

    @Column(name = "nombre_productor", nullable = false, length = 100)
    private String nombreProductor;
}
