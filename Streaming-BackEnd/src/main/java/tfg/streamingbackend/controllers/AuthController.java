package tfg.streamingbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tfg.streamingbackend.model.LoginRequestDTO;
import tfg.streamingbackend.model.LoginResponseDTO;
import tfg.streamingbackend.model.RegisterRequestDTO;
import tfg.streamingbackend.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestBody String email) {
        return ResponseEntity.ok(authService.checkEmailExists(email));
    }
}
