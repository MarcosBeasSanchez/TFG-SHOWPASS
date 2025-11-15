package tfg.proyecto.TFG.servicios;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.FileUtils;

@Service
public class ServicioImagenImpl {

	// placeHolder para evitar nulls
	private static final String PLACEHOLDER_INVITADO = "/images/placeholder-invitado.png";

	/**
	 * Guarda Base64 o URL y devuelve la ruta relativa
	 */
	public String guardarImagenBase64(String base64, String subcarpeta) throws IOException {
		if (base64 == null || base64.isBlank()) {
			return PLACEHOLDER_INVITADO;
		}

		String ruta = FileUtils.guardarImagenBase64(base64, subcarpeta);
		return ruta != null ? ruta : PLACEHOLDER_INVITADO;
	}

	/**
	 * Devuelve la imagen convertida a Base64
	 */
	public String obtenerImagenBase64(String rutaRelativa) {
		try {
			return FileUtils.leerImagenComoBase64(rutaRelativa);
		} catch (IOException e) {
			return null;
		}
	}

	/*
	 * Soporta Base64 largo → se guarda en disco.
	 * 
	 * Soporta URLs externas → devuelve tal cual.
	 * 
	 * Si no hay imagen → devuelve placeholder (/images/placeholder-invitado.png)
	 * para evitar null.
	 * 
	 * Evita warnings de React con <img src="">.
	 * 
	 * Permite obtener la imagen en Base64 con obtenerImagenBase64() si lo necesitas
	 * en frontend.
	 */
	
	public boolean eliminarArchivo(String rutaRelativa) {
	    try {
	        if (rutaRelativa == null || !rutaRelativa.startsWith("/uploads/")) {
	            return false; // No se borra placeholder o URLs externas
	        }

	        // Convertir ruta relativa en ruta absoluta
	        Path rutaAbsoluta = Paths.get(System.getProperty("user.dir") + rutaRelativa);

	        File archivo = rutaAbsoluta.toFile();

	        if (archivo.exists()) {
	            boolean eliminado = archivo.delete();
	            System.out.println(" Archivo eliminado: " + rutaAbsoluta + " → " + eliminado);
	            return eliminado;
	        }

	        System.out.println(" No existe: " + rutaAbsoluta);
	        return false;

	    } catch (Exception e) {
	        System.err.println(" Error eliminando archivo: " + rutaRelativa);
	        e.printStackTrace();
	        return false;
	    }
	}

}
