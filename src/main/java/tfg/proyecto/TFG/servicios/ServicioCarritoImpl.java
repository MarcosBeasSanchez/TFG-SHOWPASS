package tfg.proyecto.TFG.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOCarritoBajada;
import tfg.proyecto.TFG.dtos.DTOCarritoSubida;
import tfg.proyecto.TFG.modelo.Carrito;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioCarrito;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;

@Service
public class ServicioCarritoImpl implements IServicioCarrito{

	@Autowired
	RepositorioCarrito carritoRepository;
	@Autowired
	RepositorioUsuario usuarioRepository;
	@Autowired
	RepositorioEvento eventoRepository;
	@Autowired
	DtoConverter dtoConverter;

	 @Override
	    public DTOCarritoBajada obtenerCarritoPorUsuario(Long usuarioId) {
	        Usuario usuario = usuarioRepository.findById(usuarioId)
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	        Carrito carrito = carritoRepository.findByUsuario(usuario)
	                .orElseGet(() -> {
	                    Carrito nuevo = new Carrito();
	                    nuevo.setUsuario(usuario);
	                    return carritoRepository.save(nuevo);
	                });

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public DTOCarritoBajada agregarEvento(Long usuarioId, DTOCarritoSubida request) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        Evento evento = eventoRepository.findById(request.getEventoId())
	                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

	        carrito.getEventos().add(evento);
	        carritoRepository.save(carrito);

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public DTOCarritoBajada eliminarEvento(Long usuarioId, Long eventoId) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        carrito.getEventos().removeIf(e -> e != null && e.getId() == eventoId);
	        carritoRepository.save(carrito);

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public DTOCarritoBajada vaciarCarrito(Long usuarioId) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        carrito.getEventos().clear();
	        carritoRepository.save(carrito);

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public double calcularTotal(Long usuarioId) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        return carrito.getEventos().stream()
	                .mapToDouble(e -> e.getPrecio())
	                .sum();
	    }

	    @Override
	    public DTOCarritoBajada finalizarCompra(Long usuarioId) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        // Aquí puedes agregar lógica de pago
	        carrito.getEventos().clear();
	        carritoRepository.save(carrito);

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    // Método helper para obtener la entidad Carrito
	    private Carrito obtenerEntidadCarrito(Long usuarioId) {
	        Usuario usuario = usuarioRepository.findById(usuarioId)
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	        return carritoRepository.findByUsuario(usuario)
	                .orElseGet(() -> {
	                    Carrito nuevo = new Carrito();
	                    nuevo.setUsuario(usuario);
	                    return carritoRepository.save(nuevo);
	                });
	    }

}
