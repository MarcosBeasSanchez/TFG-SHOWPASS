package tfg.proyecto.TFG.servicios;

import java.util.List;

import tfg.proyecto.TFG.dtos.DTOticketBajada;
import tfg.proyecto.TFG.dtos.DTOticketSubida;

public interface IServicioTicket {
	
	DTOticketBajada insert(DTOticketSubida dtoTicket);
	boolean delete(Long id);
	DTOticketBajada obtnerTicketPorId(Long id);
	List<DTOticketBajada> obtnerTicketsPorUsuarioId(Long usuarioId);
	List<DTOticketBajada> obtnerTicketsPorEventoId(Long eventoId);
	boolean validarCodigoQR(String codigoQR);
}
