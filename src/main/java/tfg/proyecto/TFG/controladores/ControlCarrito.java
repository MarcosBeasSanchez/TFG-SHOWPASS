package tfg.proyecto.TFG.controladores;

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
import org.springframework.web.bind.annotation.RestController;

import tfg.proyecto.TFG.dtos.DTOCarritoBajada;
import tfg.proyecto.TFG.dtos.DTOCarritoSubida;
import tfg.proyecto.TFG.servicios.IServicioCarrito;

@RestController
@RequestMapping("/tfg/carrito")
@CrossOrigin(origins = "http://localhost:5173")

public class ControlCarrito {

	@Autowired
	IServicioCarrito carritoService;

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
	@PostMapping("/agregar/{usuarioId}/{eventoId}")
	public ResponseEntity<DTOCarritoBajada> agregarEvento(@PathVariable Long usuarioId, @PathVariable Long eventoId) {
		try {
			DTOCarritoBajada carrito = carritoService.agregarEvento(usuarioId, eventoId);
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
	@PostMapping("/finalizar/{usuarioId}")
	public ResponseEntity<DTOCarritoBajada> finalizarCompra(@PathVariable Long usuarioId) {
		try {
			DTOCarritoBajada carrito = carritoService.finalizarCompra(usuarioId);
			return ResponseEntity.ok(carrito);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

}
