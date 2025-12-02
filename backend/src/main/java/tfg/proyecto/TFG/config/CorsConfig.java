package tfg.proyecto.TFG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Configuración global de CORS para la aplicación.
 * Permite que solicitudes desde orígenes específicos (por ejemplo, el frontend) 
 * puedan interactuar con los endpoints del backend.
 */
@Configuration
public class CorsConfig {
	 /**
     * Configura los mapeos CORS para toda la aplicación.
     * 
     * @return WebMvcConfigurer con la configuración de CORS aplicada.
     *         - Permite todos los métodos HTTP.
     *         - Permite todos los encabezados.
     *         - Restringe los orígenes a "http://localhost" (puerto 80 por defecto).
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}
