package tfg.proyecto.TFG.servicios;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.config.FileUtils;
import tfg.proyecto.TFG.config.QRCodeGenerator;
import tfg.proyecto.TFG.dtos.DTOticketBajada;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.modelo.EstadoTicket;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Ticket;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;


/**
 * Servicio encargado de gestionar la lógica relacionada con los tickets de eventos.
 *
 * <p>Proporciona funcionalidades para:</p>
 * <ul>
 *     <li>Crear tickets y generar su código QR.</li>
 *     <li>Eliminar tickets.</li>
 *     <li>Obtener tickets por usuario, evento o ID.</li>
 *     <li>Validar códigos QR y marcar tickets como usados.</li>
 * </ul>
 */
@Service
public class ServicioTicketImpl implements IServicioTicket{
	
	 	@Autowired  
	 	RepositorioTicket ticketDAO;
	    @Autowired  
	    RepositorioEvento eventoDAO;
	    @Autowired  
	    ServicioQR servicioQR;
	    @Autowired 
	    DtoConverter dtoConverter;
	    @Autowired
	    RepositorioUsuario usuarioDAO;
	    @Autowired
	    QRCodeGenerator qrCodeGenerator;

	    /**
	     * Crea un ticket a partir de un DTO de subida, genera un código QR, lo guarda y devuelve el DTO de bajada.
	     *
	     * <p>Pasos internos:</p>
	     * <ol>
	     *     <li>Validar que usuarioId y eventoId no sean nulos.</li>
	     *     <li>Verificar existencia del usuario y del evento.</li>
	     *     <li>Crear entidad Ticket con estado VALIDO y fecha actual.</li>
	     *     <li>Generar un UUID como contenido del QR.</li>
	     *     <li>Generar la URL de validación y la imagen del QR.</li>
	     *     <li>Guardar el QR en disco y asociarlo al ticket.</li>
	     *     <li>Guardar el ticket en la base de datos y retornar el DTO.</li>
	     * </ol>
	     *
	     * @param dto DTO de subida con los datos del ticket
	     * @return DTO de bajada con los datos del ticket guardado
	     */
	    @Override
	    @Transactional
	    public DTOticketBajada insert(DTOticketSubida dto) {
	        //  Validar datos
	        if (dto.getUsuarioId() == null || dto.getEventoId() == null) {
	            throw new IllegalArgumentException("El usuarioId y eventoId son obligatorios.");
	        }
	        // Obtener usuario y evento
	        Usuario usuario = usuarioDAO.findById(dto.getUsuarioId())
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getUsuarioId()));

	        Evento evento = eventoDAO.findById(dto.getEventoId())
	                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + dto.getEventoId()));

	        // Crear ticket
	        Ticket ticket = new Ticket();
	        ticket.setUsuario(usuario);
	        ticket.setEvento(evento);
	        ticket.setPrecioPagado(dto.getPrecioPagado());
	        ticket.setEstado(EstadoTicket.VALIDO);
	        ticket.setFechaCompra(LocalDateTime.now());

	        // Generar QR con UUID
	        String claveValidacion = UUID.randomUUID().toString();	        
	        String contenidoQR = claveValidacion; //contenido aleatorio
	        String urlValidacion = "http://localhost:8080/tfg/ticket/validarQR?contenidoQR=" + claveValidacion;
	        ticket.setContenidoQR(contenidoQR); //Clave 
	        ticket.setUrlQR(urlValidacion); //URL completa

	        // Generar imagen QR y codificarla en Base64
	        byte[] imagenQR = qrCodeGenerator.generarQRBytes(urlValidacion, 300, 300);
	        String qrBase64 = Base64.getEncoder().encodeToString(imagenQR);
	       

	        // Guardar QR en disco
	        try {
	            String rutaRelativa = FileUtils.guardarImagenBase64(qrBase64, "qr");
	            ticket.setCodigoQR(rutaRelativa);
	        } catch (IOException e) {
	            throw new RuntimeException("Error al guardar el QR: " + e.getMessage(), e);
	        }

	        // Guardar ticket con QR
	        ticket = ticketDAO.save(ticket);
	        //  Devolver DTO de bajada
	        return dtoConverter.map(ticket, DTOticketBajada.class);
	    }

	    /**
	     * Elimina un ticket por su ID.
	     *
	     * @param id ID del ticket
	     * @return true si se eliminó correctamente, false si no existía
	     */
	    @Override public boolean delete(Long id) {
	        if (!ticketDAO.existsById(id)) return false;
	        ticketDAO.deleteById(id);
	        return true;
	    }
	    
	    /**
	     * Obtiene un ticket por su ID.
	     *
	     * @param id ID del ticket
	     * @return DTO de bajada con los datos del ticket
	     */
	    @Override public DTOticketBajada obtnerTicketPorId(Long id) {
	        Ticket t = ticketDAO.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
	        return dtoConverter.map(t, DTOticketBajada.class);
	    }

	    /**
	     * Obtiene todos los tickets de un usuario.
	     *
	     * @param usuarioId ID del usuario
	     * @return lista de DTOs de bajada con los tickets
	     */
	    @Override public List<DTOticketBajada> obtnerTicketsPorUsuarioId(Long usuarioId) {
	        return dtoConverter.mapAll(ticketDAO.findByUsuarioId(usuarioId), DTOticketBajada.class);
	    }

	    /**
	     * Obtiene todos los tickets de un evento.
	     *
	     * @param eventoId ID del evento
	     * @return lista de DTOs de bajada con los tickets
	     */
	    @Override public List<DTOticketBajada> obtnerTicketsPorEventoId(Long eventoId) {
	        return dtoConverter.mapAll(ticketDAO.findByEventoId(eventoId), DTOticketBajada.class);
	    }

	    /**
	     * Valida el código QR de un ticket.
	     * Si es válido y el ticket está en estado VALIDO, lo marca como USADO.
	     *
	     * @param contenidoQR Código QR a validar
	     * @return true si el ticket fue validado y marcado como USADO; false si no existe o ya estaba usado/anulado
	     */
	    @Transactional // ¡Crucial para asegurar que la actualización se guarde!
	    public boolean validarYUsarCodigoQR(String contenidoQR) {
	    	
	        // Buscar el Ticket: Debe existir y estar en estado VALIDO
	        Optional<Ticket> ticketOpt = ticketDAO.findByContenidoQRAndEstado(contenidoQR, EstadoTicket.VALIDO);

	        
	        if (ticketOpt.isPresent()) {
	            
	            // Si se encuentra (válido), procedemos a marcarlo como USADO
	            Ticket ticket = ticketOpt.get();
	            
	            // Actualizar el estado
	            ticket.setEstado(EstadoTicket.USADO);
	         
	            // Guardar la actualización en la base de datos
	            ticketDAO.save(ticket);
	            
	            return true; // Éxito: Ticket validado y su estado actualizado a USADO
	        }
	        // Fallo: El código no existe O su estado ya era USADO/ANULADO.
	        return false; 
	    }

	    /**
	     * Elimina todos los tickets de un usuario.
	     *
	     * @param usuarioId ID del usuario
	     * @return true si se eliminaron tickets; false si no existían
	     */
	    @Override public boolean eliminarTodosLosTicketsPorUsuario(Long usuarioId) {
	        List<Ticket> tickets = ticketDAO.findByUsuarioId(usuarioId);
	        if (tickets.isEmpty()) return false;
	        ticketDAO.deleteAll(tickets);
	        return true;
	    }
	    
	    /**
	     * Obtiene un ticket por ID (igual que obtnerTicketPorId).
	     *
	     * @param id ID del ticket
	     * @return DTO del ticket
	     */
	    @Override
	    public DTOticketBajada findById(Long id) {
	        Ticket ticket = ticketDAO.findById(id)
	                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + id));
	        return dtoConverter.map(ticket, DTOticketBajada.class);
	    }
		
	}