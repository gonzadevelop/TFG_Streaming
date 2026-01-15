package tfg.streamingbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.exception.auth.EmailAlrreadyExistsException;
import tfg.streamingbackend.exception.auth.EmailNotFoundException;
import tfg.streamingbackend.exception.auth.UsernameAlreadyExistsException;
import tfg.streamingbackend.mappers.UsuarioMapper;
import tfg.streamingbackend.model.LoginRequestDTO;
import tfg.streamingbackend.model.LoginResponseDTO;
import tfg.streamingbackend.model.RegisterRequestDTO;
import tfg.streamingbackend.repositorys.RolRepository;
import tfg.streamingbackend.repositorys.UsuarioRepository;
import tfg.streamingbackend.security.JwtService;

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

        String rol = "ROLE_" + request.getRol().toUpperCase();

        user.setRol(rolRepository.findByNombre(rol));

        usuarioRepository.save(user);

        return null;
    }

    public Boolean checkEmailExists(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).isPresent();
    }
}
