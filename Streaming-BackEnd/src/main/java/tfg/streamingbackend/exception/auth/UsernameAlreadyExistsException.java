package tfg.streamingbackend.exception.auth;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("El nombre de usuario " + username + " ya está registrado.");
    }
}
