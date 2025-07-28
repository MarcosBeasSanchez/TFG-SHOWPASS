package tfg.proyecto.TFG.servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOticketBajada;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.modelo.Ticket;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;

@Service
public class ServicioTicketImpl implements IServicioTicket{
	
	@Autowired
	RepositorioTicket ticketDAO;
	
	@Autowired 
	DtoConverter dtoConverter;
	
	@Override
	public DTOticketBajada insert(DTOticketSubida dtoTicket) {
		// TODO Auto-generated method stub
		
		Ticket ticket = dtoConverter.map(dtoTicket, Ticket.class);
		
		ticket.setFechaCompra(LocalDateTime.now());
		ticket.setCodigoQR(UUID.randomUUID().toString()); //genera un QR aleatorio
		
		Ticket guardado = ticketDAO.save(ticket);
		
		return dtoConverter.map(guardado, DTOticketBajada.class);
	}



	@Override
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		boolean exito =false;
		
		if(ticketDAO.existsById(id)) {
			exito = true;
			ticketDAO.deleteById(id);
		}
		
		return exito;
	}

	@Override
	public DTOticketBajada obtnerTicketPorId(Long id) {
		// TODO Auto-generated method stub
		Ticket ticket = ticketDAO.findById(id).orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
		
		return dtoConverter.map(ticket, DTOticketBajada.class);
		
	
	}

	@Override
	public List<DTOticketBajada> obtnerTicketsPorUsuarioId(Long usuarioId) {
		// TODO Auto-generated method stub
		return dtoConverter.mapAll(ticketDAO.findByUsuarioId(usuarioId), DTOticketBajada.class);
	}

	@Override
	public List<DTOticketBajada> obtnerTicketsPorEventoId(Long eventoId) {
		// TODO Auto-generated method stub
		return dtoConverter.mapAll(ticketDAO.findByEventoId(eventoId), DTOticketBajada.class);
	}

	@Override
	public boolean validarCodigoQR(String codigoQR) {
		// TODO Auto-generated method stub
		return ticketDAO.existsByCodigoQR(codigoQR);
	}

	

}
