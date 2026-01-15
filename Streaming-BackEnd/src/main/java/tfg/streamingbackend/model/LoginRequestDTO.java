package tfg.streamingbackend.model;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
