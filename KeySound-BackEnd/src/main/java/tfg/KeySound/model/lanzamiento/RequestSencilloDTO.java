package tfg.KeySound.model.lanzamiento;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class RequestSencilloDTO {
    private String nombreSencillo;
    private MultipartFile portada;
    private MultipartFile cancion;
    private List<Long> idUsuarios;
}
