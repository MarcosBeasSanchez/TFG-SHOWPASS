package tfg.proyecto.TFG.controladores;

import java.util.List;

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
import tfg.proyecto.TFG.servicios.IServicioTicket;
@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/tfg/ticket/")
public class ControlTicket {

	@Autowired
	IServicioTicket daoTicket;

	@PostMapping("insert")
	public ResponseEntity<DTOticketBajada> insertarTicket(@RequestBody DTOticketSubida dto) {
		DTOticketBajada ticket = daoTicket.insert(dto);
		return new ResponseEntity<>(ticket, HttpStatus.OK);
	}

	@DeleteMapping("delete/{id}")
	public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
		boolean eliminado = daoTicket.delete(id);
		
		if (eliminado) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
	

}
