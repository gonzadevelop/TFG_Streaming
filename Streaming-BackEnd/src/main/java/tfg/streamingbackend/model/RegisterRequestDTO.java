package tfg.streamingbackend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDTO {
    @NotBlank
    @Pattern(regexp = "\\S+", message = "El nombre de usuario no puede contener espacios en blanco")
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    @NotBlank
    @Pattern(regexp = "Masculino|Femenino|Otro", message = "El género debe ser 'Masculino', 'Femenino' o 'Otro'")
    private String genero;

    @NotBlank
    @Pattern(regexp = "Artista|Cliente", message = "El rol debe ser 'Artista' o 'Cliente'")
    private String rol;

    private String dni;
    private String telefonoContacto;
}
