package tfg.proyecto.TFG.servicios;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOticketBajada;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Ticket;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class ServicioTicketImpl implements IServicioTicket{
	
	@Autowired
	RepositorioTicket ticketDAO;
	
	@Autowired 
	DtoConverter dtoConverter;
	
	   @Autowired
	    ServicioQR servicioQR; 
	   
	@Autowired
	RepositorioEvento eventoDAO;
	
	@Override
	public DTOticketBajada insert(DTOticketSubida dtoTicket) {
		
		String qrData = dtoTicket.getUsuarioId() + "-" + dtoTicket.getEventoId() + "-" + UUID.randomUUID();
        String qrBase64 = servicioQR.generarQRBase64(qrData);
        
        
        Evento evento = eventoDAO.findById(dtoTicket.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
		
		Ticket ticket = Ticket.builder()
		        .usuarioId(dtoTicket.getUsuarioId())
		        .eventoId(dtoTicket.getEventoId())
		        .precio(dtoTicket.getPrecio())
		        .codigoQR(qrBase64) // Genera o asigna como quieras
		        .fechaCompra(LocalDateTime.now())
		        .eventoNombre(evento.getNombre())
	            .eventoImagen(evento.getImagen())
	            .eventoInicio(evento.getInicioEvento())
		        .build();
		
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
		return ticketDAO.existsByCodigoQR(codigoQR); //devuelve si exsiste o no el codigo
	}



	@Override
	public boolean eliminarTodosLosTicketsPorUsuario(Long usuarioId) {
		// TODO Auto-generated method stub
		
		 List<Ticket> tickets = ticketDAO.findByUsuarioId(usuarioId);

		    if (tickets.isEmpty()) {
		        return false;
		    }

		    ticketDAO.deleteAll(tickets);
		    return true;
	}

	

}
