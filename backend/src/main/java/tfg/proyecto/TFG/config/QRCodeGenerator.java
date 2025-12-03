package tfg.proyecto.TFG.config;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Componente Spring para generar códigos QR en memoria.
 * <p>
 * Permite generar un código QR a partir de un texto y obtenerlo como un arreglo de bytes en formato PNG.
 * Esto es útil para enviarlo por email, almacenarlo en disco, o convertirlo a Base64.
 */
@Component
public class QRCodeGenerator {

	 /**
     * Genera un código QR en formato PNG.
     *
     * @param texto Texto que contendrá el QR (por ejemplo, información de un ticket)
     * @param ancho Anchura del QR en píxeles
     * @param alto  Altura del QR en píxeles
     * @return Arreglo de bytes con la imagen PNG generada
     * @throws RuntimeException Si ocurre un error generando la matriz del QR o escribiendo la imagen
     */
    public byte[] generarQRBytes(String texto, int ancho, int alto) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // Generar la matriz del QR
            BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto);

            // Crear la imagen en memoria
            BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            // Pintar píxeles (negro/blanco)
            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    int color = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF; // Negro o blanco
                    imagen.setRGB(x, y, color);
                }
            }

            // Convertir a bytes PNG
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(imagen, "png", baos);
                return baos.toByteArray();
            }

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generando el código QR: " + e.getMessage(), e);
        }
    }
}