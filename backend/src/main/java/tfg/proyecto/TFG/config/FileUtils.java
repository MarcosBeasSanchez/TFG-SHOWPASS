package tfg.proyecto.TFG.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;


public class FileUtils {

	/**
     * Convierte un archivo MultipartFile a String Base64
     */
    public static String convertirArchivoAString(MultipartFile archivo) throws IOException {
        return Base64.getEncoder().encodeToString(archivo.getBytes());
    }

    /**
     * Guarda una imagen en disco a partir de un String Base64 o una URL.
     * Si la cadena no es Base64 válida, simplemente devuelve la misma ruta/URL.
     *
     * base64     Cadena Base64 o URL
     * subcarpeta Carpeta donde guardar (ej: "usuarios" o "eventos/12")
     * Ruta relativa del archivo (para guardar en BD)
     */
    public static String guardarImagenBase64(String base64, String subcarpeta) throws IOException {
        if (base64 == null || base64.isBlank()) {
            return null;
        }

        // 🔸 Si es una URL o una ruta ya guardada, la devolvemos tal cual
        if (base64.startsWith("http") || base64.startsWith("/uploads/") || base64.startsWith("C:\\")) {
            return base64;
        }

        // 🔸 Si tiene prefijo tipo data:image/png;base64, quitarlo
        if (base64.startsWith("data:image")) {
            int commaIndex = base64.indexOf(",");
            if (commaIndex != -1) {
                base64 = base64.substring(commaIndex + 1);
            }
        }

        // Crear carpeta destino
        String basePath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + subcarpeta;
        Files.createDirectories(Paths.get(basePath));

        // Nombre único
        String fileName = "img_" + System.currentTimeMillis() + ".png";
        Path filePath = Paths.get(basePath, fileName);

        // 🔸 Decodificar Base64 seguro
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64);
            Files.write(filePath, imageBytes);
        } catch (IllegalArgumentException e) {
            // Si la cadena no es Base64 válida, se devuelve tal cual
            return base64;
        }

        // Devuelve la ruta relativa (usada por el frontend)
        return "/uploads/" + subcarpeta + "/" + fileName;
    }
    
   
}