package tfg.proyecto.TFG.servicios;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOCarritoBajada;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.modelo.Carrito;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioCarrito;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;

@Service
public class ServicioCarritoImpl implements IServicioCarrito{

	@Autowired
	RepositorioCarrito carritoDAO;
	@Autowired
	RepositorioUsuario usuarioDAO;
	@Autowired
	RepositorioEvento eventoDAO;
	@Autowired
	DtoConverter dtoConverter;
	@Autowired
	RepositorioTicket ticketDAO;
	@Autowired
	ServicioTicketImpl servicioTicket;
	
	 @Override
	    public DTOCarritoBajada obtenerCarritoPorUsuario(Long usuarioId) {
	        Usuario usuario = usuarioDAO.findById(usuarioId)
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	        Carrito carrito = carritoDAO.findByUsuario(usuario)
	                .orElseGet(() -> {
	                    Carrito nuevo = new Carrito();
	                    nuevo.setUsuario(usuario);
	                    return carritoDAO.save(nuevo);
	                });

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public DTOCarritoBajada agregarEvento(Long usuarioId, Long eventoId) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        Evento evento = eventoDAO.findById(eventoId)
	                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

	        carrito.getEventos().add(evento);
	        carritoDAO.save(carrito);

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public DTOCarritoBajada eliminarEvento(Long usuarioId, Long eventoId) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        for (Iterator<Evento> it = carrito.getEventos().iterator(); it.hasNext();) {
	            Evento e = it.next();
	            if (e != null && e.getId() ==eventoId) {
	                it.remove();
	                break; // <- muy importante, elimina solo uno
	            }
	        }

	        carritoDAO.save(carrito);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public DTOCarritoBajada vaciarCarrito(Long usuarioId) {
	        Carrito carrito = obtenerEntidadCarrito(usuarioId);
	        carrito.getEventos().clear();
	        carritoDAO.save(carrito);

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

	        for (Evento evento : carrito.getEventos()) {
	            DTOticketSubida dtoTicket = DTOticketSubida.builder()
	                    .usuarioId(usuarioId)
	                    .eventoId(evento.getId())
	                    .precio(evento.getPrecio())
	                    .build();

	            servicioTicket.insert(dtoTicket); // usar el servicio inyectado
	        }

	        carrito.getEventos().clear();
	        carritoDAO.save(carrito);

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    // Método helper para obtener la entidad Carrito
	    private Carrito obtenerEntidadCarrito(Long usuarioId) {
	        Usuario usuario = usuarioDAO.findById(usuarioId)
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	        return carritoDAO.findByUsuario(usuario)
	                .orElseGet(() -> {
	                    Carrito nuevo = new Carrito();
	                    nuevo.setUsuario(usuario);
	                    return carritoDAO.save(nuevo);
	                });
	    }

}
