package tfg.proyecto.TFG.servicios;

import java.io.IOException;


import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.FileUtils;
@Service
public class ServicioImagenImpl  {

	/**
    * Guarda una imagen en Base64 en la carpeta indicada y devuelve la ruta relativa
    */
   public String guardarImagenBase64(String base64, String subcarpeta) throws IOException {
       if (base64 == null || base64.isEmpty()) return null;
       return FileUtils.guardarImagenBase64(base64, subcarpeta);
   }

   /**
    * Devuelve la imagen convertida a Base64 para enviar al frontend si hace falta
    */
   public String obtenerImagenBase64(String rutaRelativa) {
       try {
           return FileUtils.leerImagenComoBase64(rutaRelativa);
       } catch (IOException e) {
           return null;
       }
   }

}
