package tfg.proyecto.TFG.servicios;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

    private final PasswordEncoder passwordEncoder;

	   @Autowired private RepositorioCarrito carritoDAO;
	    @Autowired private RepositorioCarritoItem itemDAO;
	    @Autowired private RepositorioEvento eventoDAO;
	    @Autowired private RepositorioUsuario usuarioDAO;
	    @Autowired private ServicioTicketImpl servicioTicket;
	    @Autowired private DtoConverter dtoConverter;
	    
	    
	    @PersistenceContext
	    private EntityManager em;

    ServicioCarritoImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

	    @Override
	    public DTOCarritoBajada obtenerCarritoPorUsuario(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    @Transactional
	    public DTOCarritoBajada agregarItemAlCarrito(Long usuarioId, Long eventoId, int cantidad) {
	    	  Carrito carrito = obtenerOCrearCarrito(usuarioId);
	    	    Evento evento = eventoDAO.findById(eventoId)
	    	            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

	    	    CarritoItem nuevo = CarritoItem.builder()
	    	            .carrito(carrito)
	    	            .evento(evento)
	    	            .cantidad(cantidad)
	    	            .precioUnitario(evento.getPrecio())
	    	            .build();

	    	    carrito.getItems().add(nuevo);

	    	    carritoDAO.save(carrito);

	    	    // Recargar carrito desde BD con items actualizados
	    	    Carrito carritoActualizado = carritoDAO.findById(carrito.getId()).get();
	    	    return dtoConverter.map(carritoActualizado, DTOCarritoBajada.class);
	    }

	    @Override
	    @Transactional
	    public DTOCarritoBajada actualizarItem(Long usuarioId, Long itemId, int cantidad) {
	    	Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        CarritoItem item = itemDAO.findById(itemId)
	                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

	        if (!item.getCarrito().getId().equals(carrito.getId()))
	            throw new RuntimeException("El item no pertenece a este carrito");

	        item.setCantidad(cantidad);
	        itemDAO.save(item);

	        // ✅Recargar carrito
	        Carrito carritoActualizado = carritoDAO.findById(carrito.getId()).get();
	        return dtoConverter.map(carritoActualizado, DTOCarritoBajada.class);
	    }

	    @Override
	    @Transactional
	    public DTOCarritoBajada eliminarItem(Long usuarioId, Long itemId) {
	    	 CarritoItem item = itemDAO.findById(itemId)
	    	            .orElseThrow(() -> new RuntimeException("Item no encontrado"));

	    	    if (!item.getCarrito().getUsuario().getId().equals(usuarioId))
	    	        throw new RuntimeException("No tienes permiso para eliminar este item");

	    	    //  Eliminar el item
	    	    itemDAO.delete(item);

	    	    //  Forzar sincronización con la BD
	    	    em.flush();
	    	    em.clear();  // Limpia el contexto para que Hibernate recargue desde la BD

	    	    // Recargar carrito limpio desde BD
	    	    Carrito carritoActualizado = carritoDAO.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
	    	            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

	    	    return dtoConverter.map(carritoActualizado, DTOCarritoBajada.class);
	        }

	    @Override
	    @Transactional
	    public DTOCarritoBajada vaciarCarrito(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        itemDAO.deleteAll(carrito.getItems());
	        carrito.getItems().clear();
	        carritoDAO.save(carrito);
	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    @Override
	    public double calcularTotal(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        return carrito.getItems().stream()
	                .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad())
	                .sum();
	    }

	    @Override
	    @Transactional
	    public DTOCarritoBajada finalizarCompra(Long usuarioId) {
	        Carrito carrito = obtenerOCrearCarrito(usuarioId);
	        Usuario usu = usuarioDAO.findById(usuarioId)
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	        BigDecimal total = BigDecimal.ZERO;
	    

	        if (carrito.getItems().isEmpty()) {
	            throw new RuntimeException("El carrito está vacío");
	        }

	        for (CarritoItem item : carrito.getItems()) {
	        	
				double preciounitario = item.getPrecioUnitario();
				int cantidad = item.getCantidad();
				double res = preciounitario * cantidad;
				BigDecimal subtotal = BigDecimal.valueOf(res);
				total = total.add(subtotal);
	        	
	        }
	        
	        // Verificar saldo suficiente
	        if (usu.getTarjeta().getSaldo().compareTo(total) < 0) {
	            throw new RuntimeException("Saldo insuficiente para realizar la compra");
	        }
	        
	        // Registrar tickets
	        for (CarritoItem item : carrito.getItems()) {
	            for (int i = 0; i < item.getCantidad(); i++) {

	                DTOticketSubida dto = DTOticketSubida.builder()
	                        .usuarioId(usuarioId)
	                        .eventoId(item.getEvento().getId())
	                        .precioPagado(item.getPrecioUnitario())
	                        .build();

	                servicioTicket.insert(dto);
	            }
	        }

	        // Actualizar saldo del usuario
	        usu.getTarjeta().setSaldo(usu.getTarjeta().getSaldo().subtract(total));
	        usuarioDAO.save(usu);
	        System.out.println("\nSaldo actual: " + usu.getTarjeta().getSaldo());

	        // Vaciar carrito después de compra
	        itemDAO.deleteAll(carrito.getItems());
	        carrito.getItems().clear();
	        carritoDAO.save(carrito);

	        return dtoConverter.map(carrito, DTOCarritoBajada.class);
	    }

	    // Utilidad
	    private Carrito obtenerOCrearCarrito(Long usuarioId) {
	        return carritoDAO.findActivoByUsuarioId(usuarioId)
	                .orElseGet(() -> {
	                    Usuario usuario = usuarioDAO.findById(usuarioId)
	                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	                    Carrito nuevo = Carrito.builder()
	                            .usuario(usuario)
	                            .estado(EstadoCarrito.ACTIVO)
	                            .items(new ArrayList<>())
	                            .build();
	                    return carritoDAO.save(nuevo);
	                });
	    }
	}
