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
import tfg.KeySound.model.auth.LoginRequestDTO;
import tfg.KeySound.model.auth.LoginResponseDTO;
import tfg.KeySound.model.auth.RegisterRequestDTO;
import tfg.KeySound.repositorys.RolRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.JwtService;

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

    public void register(RegisterRequestDTO request) {

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

        String rol = (request.getRol() == null || request.getRol().isBlank())
                ? "ROLE_USER"
                : "ROLE_" + request.getRol().trim().toUpperCase();

        // Mapeo y guardado del usuario
        Usuario user = usuarioMapper.toEntity(request, rolRepository.findByNombre(rol));



        user.setRol(rolRepository.findByNombre(rol));

        usuarioRepository.save(user);
    }

    public Boolean checkEmailExists(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).isPresent();
    }
}
