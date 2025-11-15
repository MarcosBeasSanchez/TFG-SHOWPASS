package tfg.proyecto.TFG.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.security.Key;

public class JwtUtil {
	
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // clave secreta para firmar el token
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // puedes usar el email o el id del usuario
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

}
