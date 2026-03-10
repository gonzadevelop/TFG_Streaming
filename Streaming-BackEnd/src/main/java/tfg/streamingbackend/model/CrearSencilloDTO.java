package tfg.streamingbackend.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class CrearSencilloDTO {
    private String nombreSencillo;
    private MultipartFile portada;
    private MultipartFile cancion;
    private List<Long> idUsuarios;
}
