package tfg.proyecto.TFG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import tfg.proyecto.TFG.servicios.IServicioUsuario;

/**
 * Configuración de seguridad de la aplicación.
 * <p>
 * Incluye:
 * <ul>
 *   <li>Deshabilitación de CSRF (para APIs REST)</li>
 *   <li>Permite todas las solicitudes HTTP (puede ajustarse según roles/endpoints)</li>
 *   <li>Configuración CORS para permitir acceso desde el frontend</li>
 *   <li>Bean de PasswordEncoder (BCrypt) para codificación de contraseñas</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private IServicioUsuario servicioUsuario;

    
    /**
     * Configura el filtro de seguridad HTTP.
     * <p>
     * Actualmente permite todas las solicitudes sin autenticación.
     * Puede ajustarse para proteger rutas específicas.
     *
     * @param http Objeto HttpSecurity proporcionado por Spring Security
     * @return SecurityFilterChain configurada
     * @throws Exception Si ocurre algún error durante la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .anyRequest().permitAll();  // permite todas las solicitudes
        return http.build();
    }
    
    /**
     * Configuración de CORS para permitir solicitudes desde el frontend.
     * <p>
     * Se permiten los métodos GET, POST, PUT, DELETE y OPTIONS.
     * Se permiten todas las cabeceras y se permite enviar credenciales.
     *
     * @return CorsConfigurationSource configurada
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite el origen del frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost", "http://localhost:80")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean de codificador de contraseñas BCrypt.
     * <p>
     * Se utiliza para hashear contraseñas de usuarios antes de almacenarlas en la base de datos.
     *
     * @return PasswordEncoder usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}