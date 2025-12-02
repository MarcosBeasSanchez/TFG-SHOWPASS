package tfg.proyecto.TFG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Clase de configuración general de la aplicación.
 *
 * <p>Define beans que se pueden inyectar en cualquier parte del proyecto, incluyendo:</p>
 * <ul>
 *     <li>{@link DtoConverter} con métodos de inicialización y destrucción.</li>
 *     <li>{@link RestTemplate} para realizar llamadas HTTP a otros servicios.</li>
 * </ul>
 */
@Configuration
public class AppConfig {

	/**
     * Bean para convertir entidades a DTOs y viceversa.
     * <p>
     * Se especifican los métodos init y destroy:
     * <ul>
     *     <li>initMethod = "inicializarBean" → se ejecuta al crear el bean.</li>
     *     <li>destroyMethod = "finalizarBean" → se ejecuta al cerrar el contexto de Spring.</li>
     * </ul>
     *
     * @return instancia de {@link DtoConverter} lista para inyección
     */
    @Bean(initMethod = "inicializarBean", destroyMethod = "finalizarBean")
    DtoConverter dtoConverter() {
	    return new DtoConverter();
	}
    
    /**
     * Bean para realizar solicitudes HTTP REST.
     * <p>
     * Este RestTemplate puede ser inyectado en servicios que necesiten consumir otros microservicios.
     *
     * @return instancia de {@link RestTemplate} lista para uso
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
