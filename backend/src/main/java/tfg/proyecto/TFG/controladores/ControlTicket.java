package tfg.proyecto.TFG.controladores;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


/**
 * Controlador REST para la gestión de tickets.
 * 
 * <p>Proporciona endpoints para:
 * <ul>
 *     <li>Crear un ticket</li>
 *     <li>Eliminar un ticket individual o todos los tickets de un usuario</li>
 *     <li>Obtener tickets por id, usuario o evento</li>
 *     <li>Obtener y validar códigos QR asociados a los tickets</li>
 * </ul>
 * 
 * <p>Todos los endpoints están prefijados con <code>/tfg/ticket/</code> y permiten 
 * solicitudes CORS desde cualquier origen.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tfg/ticket/")
public class ControlTicket {

	@Autowired
	IServicioTicket daoTicket;
	
	@Autowired
	IServicioPdfEmail emailServices;

	
	/**
     * Inserta un nuevo ticket.
     *
     * @param dto DTO con los datos del ticket
     * @return DTOticketBajada creado
     */
	@PostMapping("insert")
	public ResponseEntity<DTOticketBajada> insertarTicket(@RequestBody DTOticketSubida dto) {
		DTOticketBajada ticket = daoTicket.insert(dto);
		return new ResponseEntity<>(ticket, HttpStatus.OK);
	}
	
	 /**
     * Elimina un ticket por id.
     *
     * @param id Id del ticket
     * @return Mapa con mensaje, estado y id
     */
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
	
	/**
     * Obtiene un ticket por su id.
     *
     * @param id Id del ticket
     * @return DTOticketBajada o NOT_FOUND si no existe
     */
	@GetMapping("findById/{id}")
    public ResponseEntity<DTOticketBajada> obtenerTicketPorId(@PathVariable Long id) {
        try {
            DTOticketBajada ticket = daoTicket.obtnerTicketPorId(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	/**
     * Obtiene todos los tickets de un usuario.
     *
     * @param usuarioId Id del usuario
     * @return Lista de DTOticketBajada
     */
	@GetMapping("findByUsuarioId/{usuarioId}")
    public ResponseEntity<List<DTOticketBajada>> obtenerTicketsPorUsuario(@PathVariable Long usuarioId) {
        List<DTOticketBajada> tickets = daoTicket.obtnerTicketsPorUsuarioId(usuarioId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
	
	/**
     * Obtiene todos los tickets de un evento.
     *
     * @param eventoId Id del evento
     * @return Lista de DTOticketBajada
     */
	@GetMapping("findByEventoId/{eventoId}")
    public ResponseEntity<List<DTOticketBajada>> obtenerTicketsPorEvento(@PathVariable Long eventoId) {
        List<DTOticketBajada> tickets = daoTicket.obtnerTicketsPorEventoId(eventoId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
	
	
	/**
     * Elimina todos los tickets de un usuario.
     *
     * @param usuarioId Id del usuario
     * @return Mapa con mensaje y estado
     */
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
     * Obtiene un ticket por id (DTO).
     *
     * @param id Id del ticket
     * @return DTOticketBajada
     */
    @GetMapping("/{id}")
    public ResponseEntity<DTOticketBajada> obtenerTicket(@PathVariable Long id) {
        DTOticketBajada ticket = daoTicket.findById(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Obtiene la imagen QR de un ticket en Base64.
     *
     * @param id Id del ticket
     * @return Mapa con el código QR en Base64 o NOT_FOUND si no existe
     */
    @GetMapping("/{id}/qr")
    public ResponseEntity<Map<String, String>> obtenerQR(@PathVariable Long id) {
        DTOticketBajada ticket = daoTicket.findById(id);

        if (ticket.getCodigoQR() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }		
        //coge la ruta absoluta da igual el sistema operativo
        Path rutaAbsoluta = Paths.get(System.getProperty("user.dir"), ticket.getCodigoQR());

        try {
            byte[] imagen = Files.readAllBytes(rutaAbsoluta);
            String base64 = Base64.getEncoder().encodeToString(imagen);
            Map<String, String> respuesta = Map.of("codigoQR", "data:image/png;base64," + base64);
            return ResponseEntity.ok(respuesta);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Valida un código QR de ticket y lo marca como usado si es válido.
     *
     * @param contenidoQR Contenido del código QR
     * @return true si fue validado y marcado como usado; false si no es válido o ya fue usado
     */

    @GetMapping("validarQR")
    public ResponseEntity<Boolean> validarCodigoQR(@RequestParam String contenidoQR) {
        // Llama a la función que realiza la validación Y la actualización (uso)
        boolean validadoYUsado = daoTicket.validarYUsarCodigoQR(contenidoQR);
        // Devuelve 'true' si fue exitoso (validado y marcado) o 'false' si falló.
        return new ResponseEntity<>(validadoYUsado, HttpStatus.OK);
    }
}
