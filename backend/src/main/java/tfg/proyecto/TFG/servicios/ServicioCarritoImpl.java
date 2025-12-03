package tfg.proyecto.TFG.servicios;

import java.math.BigDecimal;
import java.util.ArrayList;

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



/**
 * Implementación del servicio {@link IServicioCarrito} encargado de gestionar toda la lógica
 * relacionada con el carrito de compras.
 * 
 * Este servicio maneja operaciones como:
 * <ul>
 *     <li>Obtención o creación automática del carrito de un usuario.</li>
 *     <li>Inserción de items en el carrito.</li>
 *     <li>Actualización y eliminación de items.</li>
 *     <li>Vaciado del carrito.</li>
 *     <li>Cálculo del total y finalización de compra con generación de tickets.</li>
 * </ul>
 */
@Service
public class ServicioCarritoImpl implements IServicioCarrito{

    private final PasswordEncoder passwordEncoder;
    
    
    
    /*
     * Repositorios y servicios inyectados:
     * - carritoDAO: maneja la persistencia de Carrito.
     * - itemDAO: maneja los CarritoItem.
     * - eventoDAO: consulta eventos para validar existencia y precio.
     * - usuarioDAO: consulta datos del usuario, principalmente su tarjeta y saldo.
     * - servicioTicket: genera tickets al finalizar la compra.
     * - dtoConverter: convierte entidades a DTOs para enviar a la capa de controlador.
     */
    @Autowired private RepositorioCarrito carritoDAO;
    @Autowired private RepositorioCarritoItem itemDAO;
    @Autowired private RepositorioEvento eventoDAO;
    @Autowired private RepositorioUsuario usuarioDAO;
    @Autowired private ServicioTicketImpl servicioTicket;
    @Autowired private DtoConverter dtoConverter;

    
    /*
     * EntityManager de JPA.
     * Se utiliza para forzar sincronización y limpiar el contexto de persistencia
     * tras eliminar un item del carrito, evitando problemas de caché de JPA.
     */
    @PersistenceContext
    private EntityManager em;

    /*
     * Constructor inyectado por Spring.
     * El PasswordEncoder no se usa directamente en este servicio,
     * pero se mantiene para compatibilidad y posibles ampliaciones.
     */
    ServicioCarritoImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene el carrito activo de un usuario. Si no existe, se crea automáticamente.
     *
     * @param usuarioId el ID del usuario
     * @return un {@link DTOCarritoBajada} representando el carrito activo
     */
    @Override
    public DTOCarritoBajada obtenerCarritoPorUsuario(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return dtoConverter.map(carrito, DTOCarritoBajada.class);
    }

     /**
     * Agrega un nuevo item al carrito del usuario.
     * Si el carrito no existe, se crea automáticamente.
     *
     * @param usuarioId el ID del usuario
     * @param eventoId el ID del evento a agregar
     * @param cantidad la cantidad de entradas del evento
     * @return el {@link DTOCarritoBajada} actualizado
     * @throws RuntimeException si el evento no existe
     */
    @Override
    @Transactional
    public DTOCarritoBajada agregarItemAlCarrito(Long usuarioId, Long eventoId, int cantidad) {

        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        // Verificación de existencia del evento
        Evento evento = eventoDAO.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Creación del nuevo item
        CarritoItem nuevo = CarritoItem.builder()
                .carrito(carrito)
                .evento(evento)
                .cantidad(cantidad)
                .precioUnitario(evento.getPrecio())
                .build();

        carrito.getItems().add(nuevo);

        // Guardar cambios
        carritoDAO.save(carrito);

        // Recargar carrito para asegurar coherencia
        Carrito carritoActualizado = carritoDAO.findById(carrito.getId()).get();
        return dtoConverter.map(carritoActualizado, DTOCarritoBajada.class);
    }

    /**
     * Actualiza la cantidad de un item existente dentro del carrito.
     * Valida que el item pertenece al carrito del usuario.
     *
     * @param usuarioId el ID del usuario
     * @param itemId el ID del item a actualizar
     * @param cantidad la nueva cantidad
     * @return el {@link DTOCarritoBajada} actualizado
     * @throws RuntimeException si el item no existe o no pertenece al carrito del usuario
     */
    @Override
    @Transactional
    public DTOCarritoBajada actualizarItem(Long usuarioId, Long itemId, int cantidad) {

        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        // Verificación: el item existe
        CarritoItem item = itemDAO.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // Validación: el item debe pertenecer al carrito del usuario
        if (!item.getCarrito().getId().equals(carrito.getId()))
            throw new RuntimeException("El item no pertenece a este carrito");

        item.setCantidad(cantidad);
        itemDAO.save(item);

        Carrito carritoActualizado = carritoDAO.findById(carrito.getId()).get();
        return dtoConverter.map(carritoActualizado, DTOCarritoBajada.class);
    }

    /**
     * Elimina un item del carrito del usuario.
     * Valida que pertenece al usuario.
     *
     * @param usuarioId el ID del usuario
     * @param itemId el ID del item a eliminar
     * @return el {@link DTOCarritoBajada} actualizado
     * @throws RuntimeException si el item no existe o no pertenece al usuario
     */
    @Override
    @Transactional
    public DTOCarritoBajada eliminarItem(Long usuarioId, Long itemId) {

        CarritoItem item = itemDAO.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        
        // Validación: el item pertenece al usuario
        if (!item.getCarrito().getUsuario().getId().equals(usuarioId))
            throw new RuntimeException("No tienes permiso para eliminar este item");

        // Eliminación del item
        itemDAO.delete(item);

        // Forzar sincronización y limpiar el contexto de persistencia
        em.flush();
        em.clear();

        // Recargar carrito actual
        Carrito carritoActualizado = carritoDAO
                .findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        return dtoConverter.map(carritoActualizado, DTOCarritoBajada.class);
    }

    /**
     * Vacía todos los items del carrito del usuario.
     *
     * @param usuarioId el ID del usuario
     * @return el {@link DTOCarritoBajada} actualizado
     */
    @Override
    @Transactional
    public DTOCarritoBajada vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        itemDAO.deleteAll(carrito.getItems());
        carrito.getItems().clear();
        carritoDAO.save(carrito);
        return dtoConverter.map(carrito, DTOCarritoBajada.class);
    }

     /**
     * Calcula el importe total del carrito.
     * Multiplica cantidad por precio unitario de cada item.
     *
     * @param usuarioId el ID del usuario
     * @return el total como {@code double}
     */
    @Override
    public double calcularTotal(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return carrito.getItems().stream()
                .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad())
                .sum();
    }

    /**
     * Finaliza la compra del carrito:
     * <ul>
     *     <li>Valida saldo suficiente.</li>
     *     <li>Genera tickets por cada item del carrito.</li>
     *     <li>Descuenta saldo del usuario.</li>
     *     <li>Vacia el carrito.</li>
     * </ul>
     *
     * @param usuarioId el ID del usuario
     * @return el {@link DTOCarritoBajada} actualizado (vacío)
     * @throws RuntimeException si el carrito está vacío, el usuario no existe o saldo insuficiente
     */
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

        // Cálculo del total
        for (CarritoItem item : carrito.getItems()) {
            double res = item.getPrecioUnitario() * item.getCantidad();
            total = total.add(BigDecimal.valueOf(res));
        }

        // Verificación de saldo suficiente
        if (usu.getTarjeta().getSaldo().compareTo(total) < 0) {
            throw new RuntimeException("Saldo insuficiente para realizar la compra");
        }

        // Generación de tickets
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

        // Descontar saldo
        usu.getTarjeta().setSaldo(usu.getTarjeta().getSaldo().subtract(total));
        usuarioDAO.save(usu);

        // Vaciar carrito
        itemDAO.deleteAll(carrito.getItems());
        carrito.getItems().clear();
        carritoDAO.save(carrito);

        return dtoConverter.map(carrito, DTOCarritoBajada.class);
    }

    /**
     * Obtiene el carrito activo del usuario.
     * Si no tiene uno, se crea automáticamente.
     *
     * @param usuarioId el ID del usuario
     * @return el carrito activo como {@link Carrito}
     * @throws RuntimeException si el usuario no existe
     */
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