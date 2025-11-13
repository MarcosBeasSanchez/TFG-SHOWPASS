package tfg.proyecto.TFG.controladores;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import tfg.proyecto.TFG.dtos.DTOCarritoBajada;
import tfg.proyecto.TFG.servicios.IServicioCarrito;

@RestController
@RequestMapping("/tfg/carrito")
@CrossOrigin(origins = "http://localhost:5173")
public class ControlCarrito {

    @Autowired
    private IServicioCarrito carritoService;
    
    private final RestTemplate restTemplate = new RestTemplate();

    
    // Buscar un carrito
    @GetMapping("/{usuarioId}")
    public ResponseEntity<DTOCarritoBajada> obtenerCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoPorUsuario(usuarioId));
    }
    

    //  Agregar nuevo item (evento) al carrito
    @PostMapping("/item/{usuarioId}/{eventoId}")
    public ResponseEntity<DTOCarritoBajada> agregarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long eventoId,
            @RequestBody Map<String, Integer> body) {
        int cantidad = body.getOrDefault("cantidad", 1);
        return ResponseEntity.ok(carritoService.agregarItemAlCarrito(usuarioId, eventoId, cantidad));
    }

    //  Actualizar cantidad de un item
    @PutMapping("/item/{usuarioId}/{itemId}")
    public ResponseEntity<DTOCarritoBajada> actualizarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        int cantidad = body.getOrDefault("cantidad", 1);
        return ResponseEntity.ok(carritoService.actualizarItem(usuarioId, itemId, cantidad));
    }

    // Eliminar un item del carrito(un solo item)
    @DeleteMapping("/itemEliminar/{usuarioId}/{itemId}")
    public ResponseEntity<DTOCarritoBajada> eliminarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, itemId));
    }

    //  Vaciar carrito(todos los items)
    @DeleteMapping("/vaciar/{usuarioId}")
    public ResponseEntity<DTOCarritoBajada> vaciarCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(usuarioId));
    }

    //  Calcular total
    @GetMapping("/total/{usuarioId}")
    public ResponseEntity<Double> calcularTotal(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.calcularTotal(usuarioId));
    }

    //  Finalizar compra
    @PostMapping("/finalizar/{usuarioId}")
    public ResponseEntity<DTOCarritoBajada> finalizarCompra(@PathVariable Long usuarioId) {
    	 restTemplate.getForObject("http://localhost:8000/reload", String.class);
        return ResponseEntity.ok(carritoService.finalizarCompra(usuarioId));
    }
}
