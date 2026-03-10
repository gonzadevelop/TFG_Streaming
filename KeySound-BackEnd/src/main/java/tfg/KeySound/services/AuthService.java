package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.EmailAlrreadyExistsException;
import tfg.KeySound.exception.auth.EmailNotFoundException;
import tfg.KeySound.exception.auth.UsernameAlreadyExistsException;
import tfg.KeySound.mappers.UsuarioMapper;
import tfg.KeySound.model.LoginRequestDTO;
import tfg.KeySound.model.LoginResponseDTO;
import tfg.KeySound.model.RegisterRequestDTO;
import tfg.KeySound.repositorys.RolRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {


    // --------------- INYECCIONES POR CONSTRUCTOR ---------------
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolRepository rolRepository;


    // -------------- MÉTODOS LLAMADOS POR ENDPOINTS --------------

    public LoginResponseDTO login(LoginRequestDTO request) {


        String username = usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new EmailNotFoundException(request.getEmail()))
                .getUsername();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword())
        );

        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

        return new LoginResponseDTO(token);
    }

    public Void register(RegisterRequestDTO request) {

        // Comprbaciones contra la bd
        usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(u -> {
                    throw new EmailAlrreadyExistsException(request.getEmail());
                });
        usuarioRepository.findByUsernameIgnoreCase(request.getUsername())
                .ifPresent(u -> {
                    throw new UsernameAlreadyExistsException(request.getUsername());
                });

        // pendiente mailjet o similar para verificacion de email

        // Encriptado de la contraseña
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        // Mapeo y guardado del usuario
        Usuario user = usuarioMapper.toEntity(request);

        String rol = (request.getRol() == null || request.getRol().isBlank())
                ? "ROLE_USER"
                : "ROLE_" + request.getRol().trim().toUpperCase();

        user.setRol(rolRepository.findByNombre(rol));

        usuarioRepository.save(user);

        return null;
    }

    public Boolean checkEmailExists(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).isPresent();
    }
}
