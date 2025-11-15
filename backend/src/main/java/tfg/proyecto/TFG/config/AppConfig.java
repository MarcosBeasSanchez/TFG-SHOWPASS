package tfg.proyecto.TFG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Cargar Beans para inyectar
    @Bean(initMethod = "inicializarBean", destroyMethod = "finalizarBean")
    DtoConverter dtoConverter() {
	    return new DtoConverter();
	}
    
    // Bean para RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
