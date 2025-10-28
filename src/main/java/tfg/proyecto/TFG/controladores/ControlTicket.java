package tfg.proyecto.TFG.controladores;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tfg.proyecto.TFG.dtos.DTOticketBajada;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.servicios.IServicioPdfEmail;
import tfg.proyecto.TFG.servicios.IServicioTicket;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tfg/ticket/")
public class ControlTicket {

	@Autowired
	IServicioTicket daoTicket;
	
	@Autowired
	IServicioPdfEmail emailServices;

	@PostMapping("insert")
	public ResponseEntity<DTOticketBajada> insertarTicket(@RequestBody DTOticketSubida dto) {
		DTOticketBajada ticket = daoTicket.insert(dto);
		return new ResponseEntity<>(ticket, HttpStatus.OK);
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<Map<String, Object>> eliminarTicket(@PathVariable Long id) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        boolean eliminado = daoTicket.delete(id);

	        if (eliminado) {
	            response.put("mensaje", "Ticket eliminado correctamente.");
	            response.put("status", "success");
	            response.put("id", id);
	        } else {
	            response.put("mensaje", "No se encontró el ticket con id: " + id);
	            response.put("status", "not_found");
	        }

	        // Siempre devolvemos HTTP 200 para que Retrofit no lo trate como error
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("mensaje", "Error interno al eliminar el ticket.");
	        response.put("status", "error");
	        return ResponseEntity.ok(response);
	    }
	}
	
	@GetMapping("findById/{id}")
    public ResponseEntity<DTOticketBajada> obtenerTicketPorId(@PathVariable Long id) {
        try {
            DTOticketBajada ticket = daoTicket.obtnerTicketPorId(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	@GetMapping("findByUsuarioId/{usuarioId}")
    public ResponseEntity<List<DTOticketBajada>> obtenerTicketsPorUsuario(@PathVariable Long usuarioId) {
        List<DTOticketBajada> tickets = daoTicket.obtnerTicketsPorUsuarioId(usuarioId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
	
	@GetMapping("findByEventoId/{eventoId}")
    public ResponseEntity<List<DTOticketBajada>> obtenerTicketsPorEvento(@PathVariable Long eventoId) {
        List<DTOticketBajada> tickets = daoTicket.obtnerTicketsPorEventoId(eventoId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
	
	@GetMapping("validarQR")
    public ResponseEntity<Boolean> validarCodigoQR(@RequestParam String codigoQR) {
        boolean valido = daoTicket.validarCodigoQR(codigoQR);
        return new ResponseEntity<>(valido, HttpStatus.OK);
    }
	
	
	@DeleteMapping("delete/all/{usuarioId}")
	public ResponseEntity<Map<String, Object>> eliminarTodosLosTicketsPorUsuario(@PathVariable Long usuarioId) {		Map<String, Object> response = new HashMap<>();

	    try {
	        boolean eliminados = daoTicket.eliminarTodosLosTicketsPorUsuario(usuarioId);

	        if (eliminados) {
	            response.put("mensaje", "Todos los tickets fueron eliminados correctamente.");
	            response.put("status", "success");
	            return ResponseEntity.ok(response);
	        } else {
	            response.put("mensaje", "No se encontraron tickets para eliminar.");
	            response.put("status", "empty");
	            // Devolvemos OK también (200) para evitar que Retrofit lo marque como error
	            return ResponseEntity.ok(response);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("mensaje", "Error interno al eliminar los tickets.");
	        response.put("status", "error");
	        // También mantenemos HTTP 200, pero informamos el error en el body
	        return ResponseEntity.ok(response);
	    }
	 
	}
	
	 /**
     * Obtener ticket por id (DTO)
     */
    @GetMapping("/{id}")
    public ResponseEntity<DTOticketBajada> obtenerTicket(@PathVariable Long id) {
        DTOticketBajada ticket = daoTicket.findById(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Obtener la imagen QR directamente (image/png)
     */
    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> obtenerQR(@PathVariable Long id) {
        DTOticketBajada ticket = daoTicket.findById(id);

        if (ticket.getCodigoQR() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String rutaRelativa = ticket.getCodigoQR();
        Path rutaAbsoluta = Paths.get(System.getProperty("user.dir") + rutaRelativa);

        try {
            byte[] imagen = Files.readAllBytes(rutaAbsoluta);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    @PostMapping("/enviarPdfEmail")
    public ResponseEntity<Map<String, String>> sendPdfEmail(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String ticketId = payload.get("ticketId");
            String eventoNombre = payload.get("eventoNombre");
            String pdfBase64 = payload.get("pdfBase64");

            // Convertir el Base64 en archivo PDF temporal
            byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
            File pdfFile = File.createTempFile("ticket-", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfBytes);
            }

            //  HTML en formato válido para Java
            String subject = "Tu entrada para " + eventoNombre + " - Ticket ID " + ticketId;
            String body = "<h2>🎫 Ticket Confirmado</h2>"
                    + "<p>Tu entrada para <b>" + eventoNombre + "</b> ya está lista ✅</p>"
                    + "<p>Se adjunta el ticket en formato PDF.</p>";

            emailServices.sendPdfEmail(email, subject, body, pdfFile);

            return ResponseEntity.ok(
                    Map.of("mensaje", "📨 Ticket enviado correctamente a " + email)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", " Error enviando el correo: " + e.getMessage()));
        }
    }
    
    
}
