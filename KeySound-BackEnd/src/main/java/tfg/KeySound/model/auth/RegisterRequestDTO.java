package tfg.KeySound.model.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;
    private String nombre;
    private String apellidos;
    private String rol;
}
