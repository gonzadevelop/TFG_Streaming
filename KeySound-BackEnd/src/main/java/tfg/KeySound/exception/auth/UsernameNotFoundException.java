package tfg.KeySound.exception.auth;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String username) {
        super("El username " + username + " no se ha encontrado.");
    }
}
