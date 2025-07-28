package tfg.proyecto.TFG.servicios;

import java.util.List;

import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;

public interface IServicioEvento {
	
	DTOeventoBajada insert(DTOeventoSubida dto);
	
	List<DTOeventoBajada> obtenerTodosLosEventos();

	DTOeventoBajada actualizarEvento(Long id,  DTOeventoSubida eventodto);
	
	boolean eliminarEvento(Long id);
	
	DTOeventoBajada obtnerPorElNombre(String nombre);
}
