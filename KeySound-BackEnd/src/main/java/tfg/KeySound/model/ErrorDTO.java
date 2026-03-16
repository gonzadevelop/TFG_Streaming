package tfg.KeySound.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDTO {
    private String message;
    private LocalDateTime timestamp;
    private String className;
}
