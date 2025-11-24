package tfg.proyecto.TFG.servicios;


import java.io.IOException;

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
	

}
