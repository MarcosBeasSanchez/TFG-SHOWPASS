package tfg.proyecto.TFG.servicios;


import java.io.IOException;

import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.FileUtils;

/**
 * Servicio encargado de gestionar el almacenamiento de imágenes en la aplicación.
 * 
 * <p>Actualmente soporta la carga de imágenes en formato Base64 o URLs directas.
 * Se encarga de devolver la ruta relativa donde se almacenan, y provee un
 * placeholder por defecto si no se proporciona imagen.</p>
 */
@Service
public class ServicioImagenImpl {

	/**
     * Placeholder para invitados cuando no se proporciona imagen.
     * Evita valores nulos en los DTO o interfaces.
     */
	private static final String PLACEHOLDER_INVITADO = "/images/placeholder-invitado.png";

	  /**
     * Guarda una imagen en Base64 o devuelve un placeholder si la imagen no existe.
     *
     * <p>Este método hace lo siguiente:</p>
     * <ol>
     *     <li>Verifica si el string Base64 es nulo o vacío, en cuyo caso retorna el placeholder.</li>
     *     <li>Invoca {@link FileUtils#guardarImagenBase64(String, String)} para guardar la imagen
     *         en la subcarpeta indicada.</li>
     *     <li>Retorna la ruta relativa donde se guardó la imagen, o el placeholder si ocurre algún problema.</li>
     * </ol>
     *
     * @param base64    la imagen codificada en Base64
     * @param subcarpeta la subcarpeta dentro del almacenamiento donde se guardará la imagen
     * @return la ruta relativa de la imagen guardada o un placeholder por defecto
     * @throws IOException si ocurre un error al guardar la imagen
     */
	public String guardarImagenBase64(String base64, String subcarpeta) throws IOException {
		// Validación inicial: si la imagen es nula o vacía, usamos placeholder
		if (base64 == null || base64.isBlank()) {
			return PLACEHOLDER_INVITADO;
		}
		// Guardar la imagen usando la utilidad FileUtils
		String ruta = FileUtils.guardarImagenBase64(base64, subcarpeta);
		// Retornar la ruta obtenida o el placeholder si algo salió mal
		return ruta != null ? ruta : PLACEHOLDER_INVITADO;
	}
	

}
