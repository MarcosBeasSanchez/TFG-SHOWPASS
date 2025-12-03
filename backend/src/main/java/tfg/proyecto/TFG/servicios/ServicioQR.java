package tfg.proyecto.TFG.servicios;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;


/**
 * Servicio encargado de generar códigos QR.
 *
 * <p>Proporciona la funcionalidad de generar un código QR a partir de un texto
 * y devolverlo como una cadena Base64, lista para ser usada en imágenes HTML o PDF.</p>
 */
@Service
public class ServicioQR {
	
	 /**
     * Genera un código QR a partir de un texto y lo retorna como Base64.
     *
     * <p>El método realiza los siguientes pasos:</p>
     * <ol>
     *     <li>Crea un {@link QRCodeWriter} para codificar el texto.</li>
     *     <li>Genera una {@link com.google.zxing.common.BitMatrix} con las dimensiones del QR.</li>
     *     <li>Convierte la matriz en una {@link BufferedImage} usando {@link MatrixToImageWriter}.</li>
     *     <li>Escribe la imagen en un flujo de bytes y la convierte a Base64.</li>
     * </ol>
     *
     * @param texto Texto que se codificará en el QR
     * @return cadena Base64 que representa la imagen PNG del código QR
     * @throws RuntimeException si ocurre un error al generar el QR
     */
    public String generarQRBase64(String texto) {
        try {
        	// Crear generador de QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // Codificar el texto en una matriz de bits (250x250)
            var bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, 250, 250);

            // Convertir la matriz a imagen
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            // Escribir la imagen a un flujo de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            
            // Codificar los bytes de la imagen en Base64
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generando QR", e);
        }
    }
}
