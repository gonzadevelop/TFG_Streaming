package tfg.KeySound.exception.auth;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super("El email '" + email + "' no se ha encontrado.");
    }
}
