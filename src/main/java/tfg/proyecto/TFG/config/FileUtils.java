package tfg.proyecto.TFG.config;
import java.io.IOException;
import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    public static String convertirArchivoAString(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}