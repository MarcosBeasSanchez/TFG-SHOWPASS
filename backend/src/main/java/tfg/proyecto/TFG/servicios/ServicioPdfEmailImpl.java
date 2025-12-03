package tfg.proyecto.TFG.servicios;

import org.springframework.stereotype.Service;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio encargado de enviar correos electrónicos con archivos PDF adjuntos.
 * 
 * <p>Este servicio utiliza {@link JavaMailSender} de Spring para enviar correos
 * MIME (HTML + adjuntos). Es útil para enviar comprobantes, facturas, tickets
 * u otros documentos en formato PDF.</p>
 */
@Service
public class ServicioPdfEmailImpl implements IServicioPdfEmail {

	  /**
     * Inyección de {@link JavaMailSender} para enviar correos.
     */
	private final JavaMailSender mailSender;

	

    /**
     * Constructor con inyección de dependencias.
     *
     * @param mailSender instancia de JavaMailSender proporcionada por Spring
     */
	public ServicioPdfEmailImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
     * Envía un correo electrónico con un archivo PDF adjunto.
     *
     * <p>El método realiza los siguientes pasos:</p>
     * <ol>
     *     <li>Crea un mensaje MIME con {@link JavaMailSender#createMimeMessage()}.</li>
     *     <li>Configura destinatario, asunto y cuerpo (HTML) usando {@link MimeMessageHelper}.</li>
     *     <li>Adjunta el archivo PDF al correo.</li>
     *     <li>Envía el correo usando {@link JavaMailSender#send(MimeMessage)}.</li>
     * </ol>
     *
     * @param toEmail dirección de correo del destinatario
     * @param subject asunto del correo
     * @param body contenido HTML del correo
     * @param pdfFile archivo PDF a adjuntar
     * @throws MessagingException si ocurre algún error al construir o enviar el mensaje
     */
	public void sendPdfEmail(String toEmail, String subject, String body, File pdfFile) throws MessagingException {
		
		// Crear mensaje MIME
		MimeMessage message = mailSender.createMimeMessage();
		// Helper para configurar el correo (HTML y adjuntos)
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(toEmail);
		helper.setSubject(subject);
		helper.setText(body, true); // true = HTML

		// Adjuntar el PDF
		FileSystemResource file = new FileSystemResource(pdfFile);
		helper.addAttachment(pdfFile.getName(), file);
		
		// Enviar correo
		mailSender.send(message);
	}
}
