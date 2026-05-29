package tfg.KeySound.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tfg.KeySound.services.external.CustomUserDetailsService;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;


    /**
     * Configura la cadena de filtros de seguridad de Spring Security. Desactiva CSRF, habilita CORS con la configuración personalizada,
     * establece la política de creación de sesiones a STATELESS, configura el proveedor de autenticación
     * @param http {@link HttpSecurity} objeto que permite configurar la seguridad HTTP para la aplicación
     * @return {@link SecurityFilterChain} la cadena de filtros de seguridad configurada
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // Ahora Spring buscará el Bean definido abajo automáticamente
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configura la fuente de configuración CORS para permitir solicitudes desde el frontend en desarrollo y producción.
     * @return {@link CorsConfigurationSource} la fuente de configuración CORS configurada
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Añadimos tanto el entorno local como la URL definitiva de Firebase Hosting
        configuration.setAllowedOrigins(java.util.List.of(
                "http://localhost:4200",
                "https://angular-keysound-tfg--keysound-5480e.europe-west4.hosted.app"
        ));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura el proveedor de autenticación utilizando DaoAuthenticationProvider, que se encarga de autenticar a los usuarios
     * @return {@link AuthenticationProvider} el proveedor de autenticación configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Configura el codificador de contraseñas utilizando BCrypt, que es un algoritmo de hashing seguro para almacenar contraseñas.
     * @return {@link PasswordEncoder} el codificador de contraseñas configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el AuthenticationManager utilizando la configuración de autenticación proporcionada por Spring Security.
     * @param config {@link AuthenticationConfiguration} la configuración de autenticación proporcionada por Spring Security
     * @return {@link AuthenticationManager} el AuthenticationManager configurado
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}