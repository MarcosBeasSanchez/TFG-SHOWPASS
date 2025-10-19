package tfg.proyecto.TFG.controladores;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.MessagingException;
import tfg.proyecto.TFG.dtos.DTOCarritoBajada;
import tfg.proyecto.TFG.dtos.DTOCarritoSubida;
import tfg.proyecto.TFG.servicios.IServicioCarrito;
import tfg.proyecto.TFG.servicios.IServicioPdfEmail;

@RestController
@RequestMapping("/tfg/carrito")
@CrossOrigin(origins = "http://localhost:5173")

public class ControlCarrito {

	@Autowired
	IServicioCarrito carritoService;
	@Autowired
	IServicioPdfEmail emailService;



	// Obtener carrito de un usuario
	@GetMapping("/{usuarioId}")
	public ResponseEntity<DTOCarritoBajada> obtenerCarrito(@PathVariable Long usuarioId) {
		try {
			DTOCarritoBajada carrito = carritoService.obtenerCarritoPorUsuario(usuarioId);
			return ResponseEntity.ok(carrito);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	// Agregar un evento al carrito
	@PostMapping("/agregar/{carritoId}/{eventoId}")
	public ResponseEntity<DTOCarritoBajada> agregarEvento(
	        @PathVariable Long carritoId,
	        @PathVariable Long eventoId,
	        @RequestBody Map<String, Integer> body) {

	    int cantidad = body.getOrDefault("cantidad", 1);
	    try {
	        DTOCarritoBajada carrito = carritoService.agregarEventoPorCarrito(carritoId, eventoId, cantidad);
	        return ResponseEntity.ok(carrito);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}


	// Eliminar un evento del carrito
	@DeleteMapping("/eliminar/{usuarioId}/{eventoId}")
	public ResponseEntity<DTOCarritoBajada> eliminarEvento(@PathVariable Long usuarioId, @PathVariable Long eventoId) {
		try {
			DTOCarritoBajada carrito = carritoService.eliminarEvento(usuarioId, eventoId);
			return ResponseEntity.ok(carrito);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	// Vaciar el carrito de un usuario
	@DeleteMapping("/vaciar/{usuarioId}")
	public ResponseEntity<DTOCarritoBajada> vaciarCarrito(@PathVariable Long usuarioId) {
		try {
			DTOCarritoBajada carrito = carritoService.vaciarCarrito(usuarioId);
			return ResponseEntity.ok(carrito);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	// Calcular total del carrito
	@GetMapping("/total/{usuarioId}")
	public ResponseEntity<Double> calcularTotal(@PathVariable Long usuarioId) {
		try {
			double total = carritoService.calcularTotal(usuarioId);
			return ResponseEntity.ok(total);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Finalizar la compra
	@PostMapping("/finalizar/{carritoId}")
	public ResponseEntity<DTOCarritoBajada> finalizarCompra(@PathVariable Long carritoId) {
	    try {
	        DTOCarritoBajada dto = carritoService.finalizarCompraPorCarrito(carritoId);
	        return ResponseEntity.ok(dto);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}


	@PostMapping("/enviarPdfEmail")
	public ResponseEntity<Map<String, String>> sendTicketEmail(@RequestBody Map<String, String> payload) throws MessagingException, IOException {
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

	    // Enviar email
	    emailService.sendPdfEmail(
	            email,
	            "Tu entrada para " + eventoNombre + " - Ticket ID: " + ticketId,
	            "<p>Hola, aquí tienes tu ticket con ID <b>" + ticketId + "</b>.</p>",
	            pdfFile
	    );

	    // Devolver respuesta en formato JSON correcto
	    Map<String, String> response = new HashMap<>();
	    response.put("mensaje", "Correo enviado correctamente");
	    response.put("email", email);
	    response.put("ticketId", ticketId);

	    return ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente", "email", email));
	}
}
