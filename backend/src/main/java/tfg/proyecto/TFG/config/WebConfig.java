package tfg.proyecto.TFG.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC para exponer recursos estáticos.
 * <p>
 * Esta configuración permite que los archivos subidos (por ejemplo, imágenes de usuarios, eventos o invitados)
 * sean accesibles públicamente mediante URLs como <code>/uploads/...</code>.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	 /**
     * Configura la ubicación de los recursos estáticos.
     * <p>
     * Cualquier solicitud que comience con <code>/uploads/**</code> se mapea a la carpeta
     * física <code>uploads</code> dentro del directorio de trabajo del proyecto.
     *
     * @param registry Registro de handlers de recursos de Spring MVC
     */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 // Ruta absoluta de la carpeta uploads en el sistema
        String uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toUri().toString();

        // Mapeo: solicitudes /uploads/** → carpeta física /uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}