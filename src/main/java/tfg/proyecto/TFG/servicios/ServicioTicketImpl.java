package tfg.proyecto.TFG.servicios;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
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
	     * Crea un ticket a partir de un DTO de subida, genera su QR y devuelve el DTO de bajada.
	     */
	    @Override
	    @Transactional
	    public DTOticketBajada insert(DTOticketSubida dto) {
	        //  Validar datos
	        if (dto.getUsuarioId() == null || dto.getEventoId() == null) {
	            throw new IllegalArgumentException("El usuarioId y eventoId son obligatorios.");
	        }

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

	        // Guardar primero para generar ID
	        ticket = ticketDAO.save(ticket);

	        // Generar QR
	        String contenidoQR = "TICKET-" + ticket.getId() + "-USUARIO-" + usuario.getId();
	        byte[] imagenQR = qrCodeGenerator.generarQRBytes(contenidoQR, 300, 300);
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


	    @Override public boolean delete(Long id) {
	        if (!ticketDAO.existsById(id)) return false;
	        ticketDAO.deleteById(id);
	        return true;
	    }

	    @Override public DTOticketBajada obtnerTicketPorId(Long id) {
	        Ticket t = ticketDAO.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
	        return dtoConverter.map(t, DTOticketBajada.class);
	    }

	    @Override public List<DTOticketBajada> obtnerTicketsPorUsuarioId(Long usuarioId) {
	        return dtoConverter.mapAll(ticketDAO.findByUsuarioId(usuarioId), DTOticketBajada.class);
	    }

	    @Override public List<DTOticketBajada> obtnerTicketsPorEventoId(Long eventoId) {
	        return dtoConverter.mapAll(ticketDAO.findByEventoId(eventoId), DTOticketBajada.class);
	    }

	    @Override public boolean validarCodigoQR(String codigoQR) {
	        return ticketDAO.existsByCodigoQR(codigoQR);
	    }

	    @Override public boolean eliminarTodosLosTicketsPorUsuario(Long usuarioId) {
	        List<Ticket> tickets = ticketDAO.findByUsuarioId(usuarioId);
	        if (tickets.isEmpty()) return false;
	        ticketDAO.deleteAll(tickets);
	        return true;
	    }
	    
	    @Override
	    public DTOticketBajada findById(Long id) {
	        Ticket ticket = ticketDAO.findById(id)
	                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + id));
	        return dtoConverter.map(ticket, DTOticketBajada.class);
	    }
		
	}