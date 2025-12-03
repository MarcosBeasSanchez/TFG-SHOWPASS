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

/**
 * Controlador REST para la gestión del carrito de un usuario.
 *
 * <p>Proporciona endpoints para:
 * <ul>
 *     <li>Obtener el carrito de un usuario</li>
 *     <li>Agregar, actualizar o eliminar items del carrito</li>
 *     <li>Vaciar el carrito completo</li>
 *     <li>Calcular el total del carrito</li>
 *     <li>Finalizar la compra del carrito</li>
 * </ul>
 *
 * <p>Todos los endpoints están prefijados con <code>/tfg/carrito</code> y permiten 
 * solicitudes CORS desde cualquier origen.
 */
@RestController
@RequestMapping("/tfg/carrito")
@CrossOrigin(origins = "*") 
public class ControlCarrito {

    @Autowired
    private IServicioCarrito carritoService;
    
    private final RestTemplate restTemplate = new RestTemplate();

    
    /**
     * Obtiene el carrito completo de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return Carrito con todos los items
     */
    @GetMapping("/{usuarioId}")
    public ResponseEntity<DTOCarritoBajada> obtenerCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoPorUsuario(usuarioId));
    }
    

    /**
     * Agrega un nuevo item (evento) al carrito de un usuario.
     *
     * @param usuarioId ID del usuario
     * @param eventoId  ID del evento a agregar
     * @param body      JSON con la cantidad (opcional, default=1)
     * @return Carrito actualizado
     */
    @PostMapping("/item/{usuarioId}/{eventoId}")
    public ResponseEntity<DTOCarritoBajada> agregarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long eventoId,
            @RequestBody Map<String, Integer> body) {
        int cantidad = body.getOrDefault("cantidad", 1);
        return ResponseEntity.ok(carritoService.agregarItemAlCarrito(usuarioId, eventoId, cantidad));
    }

    /**
     * Actualiza la cantidad de un item del carrito.
     *
     * @param usuarioId ID del usuario
     * @param itemId    ID del item dentro del carrito
     * @param body      JSON con la cantidad actualizada
     * @return Carrito actualizado
     */
    @PutMapping("/item/{usuarioId}/{itemId}")
    public ResponseEntity<DTOCarritoBajada> actualizarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        int cantidad = body.getOrDefault("cantidad", 1);
        return ResponseEntity.ok(carritoService.actualizarItem(usuarioId, itemId, cantidad));
    }

    /**
     * Elimina un item específico del carrito de un usuario.
     *
     * @param usuarioId ID del usuario
     * @param itemId    ID del item a eliminar
     * @return Carrito actualizado
     */
    @DeleteMapping("/itemEliminar/{usuarioId}/{itemId}")
    public ResponseEntity<DTOCarritoBajada> eliminarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, itemId));
    }

    /**
     * Vacía todos los items del carrito de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return Carrito vacío
     */
    @DeleteMapping("/vaciar/{usuarioId}")
    public ResponseEntity<DTOCarritoBajada> vaciarCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(usuarioId));
    }

    /**
     * Calcula el total del carrito de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return Total en dinero del carrito
     */
    @GetMapping("/total/{usuarioId}")
    public ResponseEntity<Double> calcularTotal(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.calcularTotal(usuarioId));
    }

    /**
     * Finaliza la compra del carrito de un usuario.
     * <p>
     * Puede integrar lógicas de pago, generación de tickets, etc.
     *
     * @param usuarioId ID del usuario
     * @return Carrito después de finalizar la compra (normalmente vacío o con estado de compra finalizada)
     */
    @PostMapping("/finalizar/{usuarioId}")
    public ResponseEntity<DTOCarritoBajada> finalizarCompra(@PathVariable Long usuarioId) {
    	 //restTemplate.getForObject("http://localhost:8000/reload", String.class); 
        return ResponseEntity.ok(carritoService.finalizarCompra(usuarioId));
    }
}
