package tfg.proyecto.TFG.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.security.Key;

public class JwtUtil {
	
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // clave secreta para firmar el token
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora = 3600 segundos
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // puedes usar el email o el id del usuario
                .setIssuedAt(new Date(System.currentTimeMillis())) 
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1 hora
                .signWith(SECRET_KEY) //H265
                .compact();
    }

 // Extrae el email (Subject) del token
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
    
 // Valida la expiración y que el email extraído sea el esperado
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
