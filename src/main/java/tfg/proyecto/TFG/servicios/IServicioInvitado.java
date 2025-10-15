package tfg.proyecto.TFG.servicios;

import java.util.List;

import tfg.proyecto.TFG.dtos.DTOInvitadoBajada;
import tfg.proyecto.TFG.dtos.DTOInvitadoSubida;

public interface IServicioInvitado {
	List<DTOInvitadoBajada> guardarInvitados(Long eventoId, List<DTOInvitadoSubida> invitadosDto);
	List<DTOInvitadoBajada> obtenerInvitados(Long eventoId);
	void eliminarInvitados(Long eventoId);
}
