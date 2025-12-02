package tfg.proyecto.TFG.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;


/**
 * Clase de utilidades para manejo de archivos e imágenes.
 * <p>
 * Incluye métodos para convertir archivos a Base64 y guardar imágenes en disco a partir de Base64 o URLs.
 */
public class FileUtils {

	/**
     * Convierte un archivo {@link MultipartFile} a una cadena en Base64.
     *
     * @param archivo Archivo a convertir.
     * @return Cadena Base64 representando el archivo.
     * @throws IOException Si ocurre un error al leer los bytes del archivo.
     */
    public static String convertirArchivoAString(MultipartFile archivo) throws IOException {
        return Base64.getEncoder().encodeToString(archivo.getBytes());
    }

    /**
     * Guarda una imagen en disco a partir de un String Base64 o devuelve la URL/ruta original.
     * <p>
     * Reglas de funcionamiento:
     * <ul>
     *     <li>Si la cadena comienza con "http", "/uploads/" o "C:\", se asume que es una URL o ruta existente y se devuelve tal cual.</li>
     *     <li>Si la cadena tiene prefijo tipo "data:image/png;base64,", se elimina antes de decodificar.</li>
     *     <li>Se crea automáticamente la carpeta destino si no existe.</li>
     *     <li>El archivo se guarda con nombre único usando timestamp.</li>
     *     <li>Si la cadena Base64 no es válida, se devuelve tal cual.</li>
     * </ul>
     *
     * @param base64     Cadena Base64 o URL/ruta de la imagen.
     * @param subcarpeta Carpeta donde guardar la imagen (ej: "usuarios", "eventos/12").
     * @return Ruta relativa de la imagen guardada (ej: "/uploads/usuarios/img_123456.png") o la URL/ruta original.
     * @throws IOException Si ocurre un error al crear directorios o escribir el archivo.
     */
    public static String guardarImagenBase64(String base64, String subcarpeta) throws IOException {
        if (base64 == null || base64.isBlank()) {
            return null;
        }

        // Si es una URL o una ruta ya guardada, la devolvemos tal cual
        if (base64.startsWith("http") || base64.startsWith("/uploads/") || base64.startsWith("C:\\")) {
            return base64;
        }

        // Si tiene prefijo tipo data:image/png;base64, quitarlo
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

        //  Decodificar Base64 seguro
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