package tfg.proyecto.TFG.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.security.Key;

/**
 * Clase utilitaria para generar, extraer y validar tokens JWT.
 * <p>
 * Se utiliza principalmente para autenticación y autorización de usuarios.
 */
public class JwtUtil {
	
	 /** Clave secreta utilizada para firmar los tokens JWT */
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	 /** Tiempo de expiración del token en milisegundos (1 hora) */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;
    
    /**
     * Genera un token JWT para un usuario dado usando su email como subject.
     *
     * @param email Email del usuario (se almacena en el subject del token)
     * @return Token JWT firmado
     */
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // puedes usar el email o el id del usuario
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiración
                .signWith(SECRET_KEY) // Firma con la clave secreta
                .compact();
    }

    /**
     * Extrae el email (subject) de un token JWT.
     *
     * @param token Token JWT
     * @return Email contenido en el subject del token, o null si no es válido
     */
    public static String extractEmail(String token) {
        try {
            return Jwts.parser()
                       .setSigningKey(SECRET_KEY)
                       .parseClaimsJws(token)
                       .getBody()
                       .getSubject();
        } catch (Exception e) {
        	System.err.println(e.getMessage());
            return null;
        }
    }
    
    /**
    * Valida un token JWT.
    * <p>
    * Comprueba que:
    * <ul>
    *     <li>El token no esté expirado</li>
    *     <li>La firma sea válida</li>
    *     <li>El email extraído coincida con el esperado</li>
    * </ul>
    *
    * @param token Token JWT a validar
    * @param expectedEmail Email esperado (subject)
    * @return true si el token es válido y corresponde al email, false en caso contrario
    */
    public static Boolean validateToken(String token, String expectedEmail) {
        final String extractedEmail = extractEmail(token);
        
        if (extractedEmail == null || !extractedEmail.equals(expectedEmail)) {
            return false; // El email no coincide o no se pudo extraer
        }
        
        try {
            // Esto comprueba la firma y la fecha de expiración
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
        	System.err.println(e.getMessage());
            return false;
        }
    }

}
