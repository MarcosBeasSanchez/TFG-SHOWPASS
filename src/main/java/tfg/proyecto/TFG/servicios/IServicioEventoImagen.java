package tfg.proyecto.TFG.servicios;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tfg.proyecto.TFG.dtos.DTOEventoImagenBajada;
import tfg.proyecto.TFG.dtos.DTOEventoImagenSubida;
import tfg.proyecto.TFG.modelo.EventoImagen;

public interface IServicioEventoImagen{
	
	List<DTOEventoImagenBajada> guardarCarrusel(Long eventoId, List<DTOEventoImagenSubida>imagenes);
	List<DTOEventoImagenBajada> obtenerCarrusel(Long eventoId);
	void eliminarCarrusel(Long eventoId);
}

