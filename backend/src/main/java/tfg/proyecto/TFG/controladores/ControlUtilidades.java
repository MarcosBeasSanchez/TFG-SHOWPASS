package tfg.proyecto.TFG.controladores;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import tfg.proyecto.TFG.dtos.DTOEventoDataIA;
import tfg.proyecto.TFG.dtos.DTOInvitadoBajada;
import tfg.proyecto.TFG.dtos.DTOUsuarioDataIA;
import tfg.proyecto.TFG.dtos.DTOticketBajada;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;
import tfg.proyecto.TFG.servicios.IServicioPdfEmail;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tfg/utilidades/")
public class ControlUtilidades {

	@Autowired
	IServicioPdfEmail daoMail;
	
	@Autowired
	RepositorioEvento eventoDAO;
	@Autowired
	RepositorioUsuario usuarioDAO;
	@Autowired
	RepositorioTicket ticketDAO;

	
	@Value("${microservicio.recomendacion.url:http://127.0.0.1:8000}")
    private  String microServicioURL;  
	

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
		// Reemplazar la línea de daoMail.sendPdfEmail(...) con esta versión:
		// Usar tu servicio para enviar el PDF
		daoMail.sendPdfEmail(
		    email, 
		    "=?UTF-8?B?" + Base64.getEncoder().encodeToString(("¡Tu entrada para " + eventoNombre + " está lista! 🎉").getBytes("UTF-8")) + "?=", // Asunto con codificación forzada
		    "<html>"
		    + "<head><meta charset=\"UTF-8\"></head>" 
		    + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6;\">"
		 
		    + "<div style=\"max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;\">"
		    + "<h2 style=\"color: #4CAF50;\">¡Compra Exitosa!</h2>"

		    + "<p>Hola, <b>" + email + "</b>.</p>"
		    // ...
		    + "<p>¡Tu aventura con <b>SHOWPASS</b> comienza ahora! Estamos encantados de confirmar tu compra. </p>"
		    + "<p>En este correo adjuntamos tu <b>ticket</b> para:</p>"
		    + "<h3 margin-top: 15px;\">" + eventoNombre + "</h3>"

		    + "<p style=\"font-size: 14px; color: #555;\">"
		    + "    <b>ID de Ticket:</b> <b style=\"color: #1a73e8;\">" + ticketId + "</b>" + "</p>"

		    + "<p>Guarda el archivo PDF adjunto de forma segura y tenlo listo en tu móvil o impreso para acceder rápidamente al recinto. ¡Solo tienes que escanear tu código!</p>"
		    + "<p style=\"margin-top: 25px;\">¡Gracias por elegir SHOWPASS! ¡Nos vemos en el evento!</p>"
		    + "<p style=\"font-size: 12px; color: #888;\">" + "     El equipo de ShowPass" + "</p>"
		    + "</div>" 
		    + "</body>"
		    + "</html>",
		    pdfFile
		);
		// Borrar archivo temporal
		pdfFile.delete();

		return "Correo enviado a " + email;
	}
	
	
	@GetMapping("/data")
	public ResponseEntity<Map<String, Object>> getRecommendationData() {
	    // Usuarios con sus tickets
	    List<DTOUsuarioDataIA> usuarios = StreamSupport.stream(usuarioDAO.findAll().spliterator(), false)
	        .map(u -> new DTOUsuarioDataIA(
	            u.getId(),
	            u.getNombre(),
	            u.getTickets().stream()
	                .map(t -> new DTOticketBajada(
	                    t.getId(),
	                    t.getCodigoQR(),
	                    t.getContenidoQR(),
	                    t.getUrlQR(),
	                    t.getFechaCompra(),
	                    t.getPrecioPagado(),
	                    t.getEstado(),
	                    t.getUsuario().getId(),
	                    t.getUsuario().getNombre(),
	                    t.getEvento().getId(),
	                    t.getEvento().getNombre()
	                ))
	                .toList()
	        ))
	        .toList();

	    // Eventos
	    List<DTOEventoDataIA> eventos = StreamSupport.stream(eventoDAO.findAll().spliterator(), false)
	        .map(e -> new DTOEventoDataIA(
	            e.getId(),
	            e.getCategoria(),
	            e.getDescripcion(),
	            e.getLocalizacion(),
	            e.getInvitados().stream()
	                .map(i -> new DTOInvitadoBajada(i.getId(), i.getNombre(), null, null, null))
	                .toList()
	        ))
	        .toList();

	    // Respuesta final
	    Map<String, Object> data = new HashMap<>();
	    data.put("usuarios", usuarios);
	    data.put("eventos", eventos);

	    return ResponseEntity.ok(data);
	    }
}
