package tfg.proyecto.TFG.servicios;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.dtos.DTOEventoImagenBajada;
import tfg.proyecto.TFG.dtos.DTOEventoImagenSubida;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.EventoImagen;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioEventoImagen;

@Service
public class ServicioEventoImagenImpl implements IServicioEventoImagen {

	 @Autowired
	    private RepositorioEventoImagen eventoImagenDAO;

	    @Autowired
	    private RepositorioEvento eventoDAO;

	    @Autowired
	    private ServicioImagenImpl servicioImagen;

	    /**
	     * Guarda las imágenes de un carrusel asociadas a un evento.
	     */
	    @Transactional
	    public List<DTOEventoImagenBajada> guardarCarrusel(Long eventoId, List<DTOEventoImagenSubida> imagenes) {
	        Evento evento = eventoDAO.findById(eventoId)
	                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

	        // Elimina las anteriores si las hay
	        eventoImagenDAO.deleteByEventoId(eventoId);

	        // Guarda nuevas
	        return imagenes.stream().map((dto) -> {
	            try {
	                String ruta = servicioImagen.guardarImagenBase64(dto.getUrl(), "eventos/" + eventoId);
	                EventoImagen img = EventoImagen.builder()
	                        .evento(evento)
	                        .url(ruta)
	                        .build();
	                eventoImagenDAO.save(img);
	                return DTOEventoImagenBajada.builder()
	                        .id(img.getId())
	                        .url(ruta)
	                        .build();
	            } catch (IOException e) {
	                throw new RuntimeException("Error guardando imagen del evento", e);
	            }
	        }).collect(Collectors.toList());
	    }

	    /**
	     * Obtiene todas las imágenes del carrusel de un evento
	     */
	    public List<DTOEventoImagenBajada> obtenerCarrusel(Long eventoId) {
	        return eventoImagenDAO.findByEventoId(eventoId)
	                .stream()
	                .map(img -> DTOEventoImagenBajada.builder()
	                        .id(img.getId())
	                        .url(img.getUrl())
	                        .build())
	                .collect(Collectors.toList());
	    }

	    /**
	     * Elimina todas las imágenes del carrusel
	     */
	    @Transactional
	    public void eliminarCarrusel(Long eventoId) {
	        eventoImagenDAO.deleteByEventoId(eventoId);
	    }

}
