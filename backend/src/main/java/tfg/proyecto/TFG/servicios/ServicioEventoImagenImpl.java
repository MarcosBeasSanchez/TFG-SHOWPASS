package tfg.proyecto.TFG.servicios;

import java.io.IOException;
import java.util.List;
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


/**
 * Implementación del servicio {@link IServicioEventoImagen} encargado de gestionar
 * las imágenes asociadas a los eventos.
 * 
 * Proporciona funcionalidades para:
 * <ul>
 *     <li>Guardar imágenes de un carrusel asociadas a un evento.</li>
 *     <li>Obtener todas las imágenes de un evento.</li>
 *     <li>Eliminar todas las imágenes de un evento.</li>
 * </ul>
 */
@Service
public class ServicioEventoImagenImpl implements IServicioEventoImagen {

	/*
		     * Repositorios y servicios inyectados:
		     * - eventoImagenDAO: Permite operaciones CRUD y consultas personalizadas como findByEventoId o deleteByEventoId.
		     * - eventoDAO:  usado para verificar que un evento existe antes de asociarle imágenes.
		     * - servicioImageb: genera tickets al finalizar la compra.
		     */
		 	@Autowired
		    private RepositorioEventoImagen eventoImagenDAO;
	
		    @Autowired
		    private RepositorioEvento eventoDAO;
	
		    @Autowired
		    private ServicioImagenImpl servicioImagen;
		    
		    /**
		     * Guarda las imágenes de un carrusel asociadas a un evento.
		     * <p>
		     * Si el evento ya tiene imágenes, se eliminan antes de agregar las nuevas.
		     * Cada imagen se guarda físicamente y luego se persiste la referencia en la base de datos.
		     * </p>
		     *
		     * @param eventoId el ID del evento
		     * @param imagenes lista de DTOs de imágenes a guardar
		     * @return lista de DTOs con la información de las imágenes guardadas
		     * @throws RuntimeException si el evento no existe o ocurre un error al guardar la imagen
		     */
		    @Transactional
		    public List<DTOEventoImagenBajada> guardarCarrusel(Long eventoId, List<DTOEventoImagenSubida> imagenes) {
		        Evento evento = eventoDAO.findById(eventoId)
		                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
	
		        // Eliminación de imágenes previas asociadas al evento
		        eventoImagenDAO.deleteByEventoId(eventoId);
	
		        // Guardar cada imagen nueva y construir DTO de salida
		        return imagenes.stream().map((dto) -> {
		            try {
		            	 // Guardado de la imagen en el servidor, ruta: eventos/{eventoId}
		                String ruta = servicioImagen.guardarImagenBase64(dto.getUrl(), "eventos/" + eventoId);
		             // Creación de la entidad EventoImagen
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
		     * Obtiene todas las imágenes del carrusel de un evento.
		     *
		     * @param eventoId el ID del evento
		     * @return lista de DTOs con la información de las imágenes
		     */
		    public List<DTOEventoImagenBajada> obtenerCarrusel(Long eventoId) {
		    	 // Consulta todas las imágenes asociadas al evento y las convierte a DTOs
		        return eventoImagenDAO.findByEventoId(eventoId)
		                .stream()
		                .map(img -> DTOEventoImagenBajada.builder()
		                        .id(img.getId())
		                        .url(img.getUrl())
		                        .build())
		                .collect(Collectors.toList());
		    }
	
		    /**
		     * Elimina todas las imágenes del carrusel de un evento.
		     *
		     * @param eventoId el ID del evento
		     */
		    @Transactional
		    public void eliminarCarrusel(Long eventoId) {
		        eventoImagenDAO.deleteByEventoId(eventoId);
		    }
	

}
