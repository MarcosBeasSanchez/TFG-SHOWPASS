package tfg.proyecto.TFG.controladores;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import tfg.proyecto.TFG.servicios.IServicioPdfEmail;
import tfg.proyecto.TFG.servicios.IServicioTicket;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tfg/utilidades/")
public class ControlUtilidades {

	@Autowired
	IServicioPdfEmail daoMail;

	@PostMapping("/enviarPdfEmail")
	public String enviarPdfEmail(@RequestBody Map<String, String> payload) throws MessagingException, IOException {
		String email = payload.get("email");
		String ticketId = payload.get("ticketId");
		String eventoNombre = payload.get("eventoNombre");
		String pdfBase64 = payload.get("pdfBase64");

		// Convertir base64 a archivo temporal
		byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
		File pdfFile = File.createTempFile("ticket-", ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			fos.write(pdfBytes);
		}

		// Usar tu servicio para enviar el PDF
		daoMail.sendPdfEmail(email, "Tu entrada para " + eventoNombre + " - Ticket ID: " + ticketId,
				"<p>Hola, aquí tienes tu ticket con ID <b>" + ticketId + "</b>.</p>", pdfFile);

		// Borrar archivo temporal
		pdfFile.delete();

		return "Correo enviado a " + email;
	}
}
