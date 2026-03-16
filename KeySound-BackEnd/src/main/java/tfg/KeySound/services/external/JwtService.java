package tfg.KeySound.services.external;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secret;
    @Value("${JWT_EXPIRATION}")
    private String expiration;

    /**
     * Genera una clave segura para firmar el token JWT.
     * @return {@link SecretKey} generado para la firma del token.
     */
    private SecretKey generateSecureKey() {
        return Jwts.SIG.HS256.key().build();
    }

    /**
     * Obtiene la clave de firma a partir de la cadena secreta configurada.
     * @return {@link SecretKey} para la firma del token JWT.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);

    }

    /**
     * Genera un token JWT para el usuario autenticado.
     * @param userDetails {@link UserDetails} Detalles del usuario para el cual se generará el token.
     * @return {@link String} token JWT generado para el usuario.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        claims.put("roles", roles);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * Integer.parseInt(expiration)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }


    /**
     * Extrae el nombre de usuario del token JWT.
     * @param token {@link String} token JWT del cual se extraerá el nombre de usuario.
     * @return {@link String} nombre de usuario extraído del token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un reclamo específico del token JWT utilizando una función de resolución de reclamos.
     * @param token {@link String}
     * @param claimsResolver {@link Function} función que define cómo resolver el reclamo a partir de los reclamos extraídos.
     * @param <T> Tipo del reclamo que se desea extraer.
     * @return {@link T} El valor del reclamo extraído del token JWT, según lo definido por la función de resolución de reclamos.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Valida si el token JWT es válido para el usuario autenticado.
     * @param token {@link String} token JWT que se desea validar.
     * @param userDetails {@link UserDetails} detalles del usuario para el cual se validará el token.
     * @return {@link Boolean} true si el token es válido para el usuario, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token JWT ha expirado.
     * @param token {@link String} token JWT que se desea verificar.
     * @return {@link Boolean} true si el token ha expirado, false en caso contrario.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Extrae todos los reclamos del token JWT.
     * @param token {@link String} token JWT del cual se extraerán los reclamos.
     * @return {@link Claims} objeto que contiene todos los reclamos extraídos del token JWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}

