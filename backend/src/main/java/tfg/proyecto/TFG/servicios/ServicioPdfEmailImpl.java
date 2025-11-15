package tfg.proyecto.TFG.servicios;

import org.springframework.stereotype.Service;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ServicioPdfEmailImpl implements IServicioPdfEmail {

	private final JavaMailSender mailSender;

	public ServicioPdfEmailImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendPdfEmail(String toEmail, String subject, String body, File pdfFile) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(toEmail);
		helper.setSubject(subject);
		helper.setText(body, true); // true = HTML

		// Adjuntar el PDF
		FileSystemResource file = new FileSystemResource(pdfFile);
		helper.addAttachment(pdfFile.getName(), file);

		mailSender.send(message);
	}
}
