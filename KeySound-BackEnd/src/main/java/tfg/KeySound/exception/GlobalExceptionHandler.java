package tfg.KeySound.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import tfg.KeySound.model.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import javax.naming.SizeLimitExceededException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manejador genérico para todas las excepciones no manejadas específicamente
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleAllExceptions(Exception ex) {
        ex.printStackTrace();

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, "Ha ocurrido un error inesperado: " + ex.getMessage());
    }

    /**
     * Manejador específico para las excepciones de autenticación
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex, "Error de autenticación: " + ex.getMessage());
    }

    /**
     * Manejador específico para las excepciones relacionadas con el tamaño de carga de archivos
     */
    @ExceptionHandler({
            MaxUploadSizeExceededException.class,
            SizeLimitExceededException.class,
            FileSizeLimitExceededException.class
    })
    public ResponseEntity<ErrorDTO> handlerMaxSizeExceptions(Exception ex) {

        // Diferenciamos entre las excepciones de tamaño de carga para proporcionar un mensaje más específico al usuario.
        String massage = ex instanceof MaxUploadSizeExceededException ?
                "El tamaño total de la carga excede el límite permitido (60MB)." :
                "El archivo excede el tamaño máximo permitido (10MB).";

        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, ex, massage);

    }

    /**
     * Manejador específico para las excepciones de validación de argumentos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, "Error de validación: " + ex.getMessage());
    }

    /**
     * Manejador específico para las excepciones personalizadas de KeySound
     */
    @ExceptionHandler(KeySoundException.class)
    public ResponseEntity<ErrorDTO> handlerKeySoundExceptions(KeySoundException ex) {
        return buildResponse(ex);
    }

    /**
     * Metodo auxiliar para construir la respuesta de error
     */
    private ResponseEntity<ErrorDTO> buildResponse(KeySoundException ex) {
        return ResponseEntity.status(ex.getStatus()).body(
                ErrorDTO.builder()
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .className(ex.getClass().getName())
                        .build()
        );
    }

    /**
     * Metodo auxiliar sobrecargado para construir la respuesta de error con un mensaje personalizado
     */
    private ResponseEntity<ErrorDTO> buildResponse(HttpStatus status, Exception ex, String massage) {
        return ResponseEntity.status(status).body(
                ErrorDTO.builder()
                        .message(massage)
                        .timestamp(LocalDateTime.now())
                        .className(ex.getClass().getName())
                        .build()
        );
    }
}
