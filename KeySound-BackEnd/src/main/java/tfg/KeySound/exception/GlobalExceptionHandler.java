package tfg.KeySound.exception;

import tfg.KeySound.exception.auth.EmailAlrreadyExistsException;
import tfg.KeySound.exception.auth.EmailNotFoundException;
import tfg.KeySound.exception.auth.UsernameAlreadyExistsException;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.model.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorDTO.builder()
                        .message("Error genérico: " + ex.getMessage())
                        .timestamp(java.time.LocalDateTime.now())
                        .className(ex.getClass().getName())
                        .build()
        );
    }

    @ExceptionHandler({
            UsernameAlreadyExistsException.class,
            EmailAlrreadyExistsException.class,
            EmailNotFoundException.class,
            UsernameNotFoundException.class,
    })
    public ResponseEntity<ErrorDTO> handleConflictExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorDTO.builder()
                        .message(ex.getMessage())
                        .timestamp(java.time.LocalDateTime.now())
                        .className(ex.getClass().getName())
                        .build()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorDTO.builder()
                        .message(ex.getMessage())
                        .timestamp(java.time.LocalDateTime.now())
                        .className(ex.getClass().getName())
                        .build()
        );
    }
}
