package tfg.proyecto.TFG.servicios;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOCarritoBajada;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.modelo.Carrito;
import tfg.proyecto.TFG.modelo.CarritoItem;
import tfg.proyecto.TFG.modelo.EstadoCarrito;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioCarrito;
import tfg.proyecto.TFG.repositorio.RepositorioCarritoItem;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;

@Service
public class ServicioCarritoImpl implements IServicioCarrito{

	 @Autowired  RepositorioCarrito carritoDAO;
	    @Autowired  RepositorioCarritoItem itemDAO;
	    @Autowired  RepositorioEvento eventoDAO;
	    @Autowired  RepositorioUsuario usuarioDAO;
	    @Autowired  ServicioTicketImpl servicioTicket;
	    @Autowired  DtoConverter dtoConverter;
	    
	    
	    
	    @Override
	    public DTOCarritoBajada obtenerCarritoPorUsuario(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public DTOCarritoBajada agregarEvento(Long usuarioId, Long eventoId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        Evento evento = eventoDAO.findById(eventoId)
	                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

	        Optional<CarritoItem> existente = itemDAO.findByCarritoAndEvento(carrito.getId(), eventoId);
	        if (existente.isPresent()) {
	            CarritoItem item = existente.get();
	            item.setCantidad(item.getCantidad() + 1);
	            itemDAO.save(item);
	        } else {
	            CarritoItem nuevo = CarritoItem.builder()
	                    .carrito(carrito)
	                    .evento(evento)
	                    .cantidad(1)
	                    .build();
	            itemDAO.save(nuevo);
	        }

	        return dtoConverter.map(obtenerOCrearCarrito(usuarioId), DTOCarritoBajada.class);
	    }


	 @Override
	    public DTOCarritoBajada eliminarEvento(Long usuarioId, Long eventoId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        itemDAO.findByCarritoAndEvento(carrito.getId(), eventoId)
	                .ifPresent(itemDAO::delete);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }


	 @Override
	    public DTOCarritoBajada vaciarCarrito(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        carrito.getItems().clear();
	        carritoDAO.save(carrito);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	 @Override
	    public double calcularTotal(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        return carrito.getItems().stream()
	                .mapToDouble(i -> i.getEvento().getPrecio() * i.getCantidad())
	                .sum();
	    }

	 @Override
	    public DTOCarritoBajada finalizarCompra(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);

	        for (CarritoItem item : carrito.getItems()) {
	            for (int i = 0; i < item.getCantidad(); i++) {
	                DTOticketSubida dtoTicket = DTOticketSubida.builder()
	                        .usuarioId(usuarioId)
	                        .eventoId(item.getEvento().getId())
	                        .precio(item.getEvento().getPrecio())
	                        .build();
	                servicioTicket.insert(dtoTicket);
	            }
	        }

	        carrito.getItems().clear();
	        carrito.setEstado(EstadoCarrito.FINALIZADO);
	        carritoDAO.save(carrito);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	 
	    @Override
	    public DTOCarritoBajada actualizarCantidad(Long usuarioId, Long eventoId, int cantidad) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        CarritoItem item = itemDAO.findByCarritoAndEvento(carrito.getId(), eventoId)
	                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
	        item.setCantidad(cantidad);
	        itemDAO.save(item);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    // Método helper para obtener la entidad Carrito de un usuario
		private Carrito obtenerOCrearCarrito(Long usuarioId) {
			 // Busca directamente por el ID del usuario
	        return carritoDAO.findActivoByUsuarioId(usuarioId)
	                .orElseGet(() -> {
	                    Usuario usuario = usuarioDAO.findById(usuarioId)
	                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	                    Carrito nuevo = new Carrito();
	                    nuevo.setUsuario(usuario);
	                    nuevo.setEstado(EstadoCarrito.ACTIVO);
	                    return carritoDAO.save(nuevo);
	                });
	    }

}
