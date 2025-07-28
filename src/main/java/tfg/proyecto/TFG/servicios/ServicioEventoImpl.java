package tfg.proyecto.TFG.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Invitado;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;

@Service
public class ServicioEventoImpl implements IServicioEvento{
	
	@Autowired
	RepositorioEvento eventoDAO;
	
	@Autowired
	DtoConverter dtoConverter;

	@Override
	public DTOeventoBajada insert(DTOeventoSubida dto) {
		// TODO Auto-generated method stub
		
		Evento evento = dtoConverter.map(dto, Evento.class);
		Evento guardado= eventoDAO.save(evento);
			
		return dtoConverter.map(guardado, DTOeventoBajada.class);
		
	}

	@Override
	public List<DTOeventoBajada> obtenerTodosLosEventos() {
		// TODO Auto-generated method stub
		return dtoConverter.mapAll((List<Evento>)eventoDAO.findAll(), DTOeventoBajada.class);
	}

	@Override
	public DTOeventoBajada actualizarEvento(Long id,DTOeventoSubida eventodto) {
		// TODO Auto-generated method stub
		 Evento existente = eventoDAO.findById(id)
	                .orElseThrow(() -> new RuntimeException("Evento no encontrado para actualizar"));

	        existente.setNombre(eventodto.getNombre());
	        existente.setLocalizacion(eventodto.getLocalizacion());
	        existente.setInicioEvento(eventodto.getInicioEvento());
	        existente.setFinEvento(eventodto.getFinEvento());
	        existente.setInvitados(dtoConverter.mapAll(eventodto.getInvitados(), Invitado.class));

	        Evento actualizado = eventoDAO.save(existente);
	        return dtoConverter.map(actualizado, DTOeventoBajada.class);

	}

	@Override
	public boolean eliminarEvento(Long id) {
		// TODO Auto-generated method stub
		boolean exito =false;
		
		if(eventoDAO.existsById(id)) {
			exito = true;
			eventoDAO.deleteById(id);
		}
		
		return exito;
	}

	@Override
	public DTOeventoBajada obtnerPorElNombre(String nombre) {
		// TODO Auto-generated method stub
		Evento evento = eventoDAO.findByNombre(nombre)
	            .orElseThrow(() -> new RuntimeException(nombre + "no encontrado"));
	    return dtoConverter.map(evento, DTOeventoBajada.class);
	}
	
	
}
