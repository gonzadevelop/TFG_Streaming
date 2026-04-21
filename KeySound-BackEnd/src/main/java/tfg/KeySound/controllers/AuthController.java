package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tfg.KeySound.model.auth.LoginRequestDTO;
import tfg.KeySound.model.auth.LoginResponseDTO;
import tfg.KeySound.model.auth.RegisterRequestDTO;
import tfg.KeySound.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Servicio para gestionar la lógica de negocio.
     */
    private final AuthService authService;

    /**
     * Endpoint para iniciar sesión.
     * @param request {@link LoginRequestDTO}
     * @return {@link ResponseEntity}&lt;{@link LoginResponseDTO}&gt Devuelve un status 200 (OK)
     * @throws tfg.KeySound.exception.auth.EmailAlrreadyExistsException Status 409 (CONFLICT)
     * @throws tfg.KeySound.exception.auth.UsernameAlreadyExistsException Status 409 (CONFLICT)
     * @apiNote {@code POST /api/auth/login}
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * @param request {@link RegisterRequestDTO}
     * @return {@link ResponseEntity}&lt;{@link Void}&gt Devuelve un status 201 (CREATED) si el registro se realiza correctamente
     * @throws tfg.KeySound.exception.auth.EmailNotFoundException Status 404 (NOT_FOUND)
     * @apiNote {@code POST /api/auth/register}
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para verificar si un email ya está registrado.
     * @param email {@link String}
     * @return {@link ResponseEntity}&lt;{@link Boolean}&gt; Devuelve true si el email ya existe, false si no existe
     * @apiNote {@code POST /api/auth/check-email}
     */
    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestBody String email) {
        return ResponseEntity.ok(authService.checkEmailExists(email));
    }
}
