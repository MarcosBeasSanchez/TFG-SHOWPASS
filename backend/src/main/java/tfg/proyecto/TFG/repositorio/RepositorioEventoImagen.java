package tfg.proyecto.TFG.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.modelo.EventoImagen;

/**
 * Repositorio JPA para gestionar las imágenes asociadas a un evento.
 * Proporciona métodos para eliminar imágenes por evento, obtener sus URLs
 * y recuperar todas las imágenes relacionadas con un evento específico.
 */
@Repository
public interface RepositorioEventoImagen extends CrudRepository<EventoImagen, Long> {
	
	/**
     * Elimina todas las imágenes asociadas a un evento según su ID.
     *
     * @param eventoId el ID del evento cuyas imágenes deben eliminarse.
     */
	 @Transactional
	 @Modifying
	 @Query("DELETE FROM EventoImagen ei WHERE ei.evento.id = :eventoId")
	 void deleteByEventoId(@Param("eventoId") Long eventoId);
	 
	 
	 /**
	 * Obtiene las URLs de todas las imágenes asociadas a un evento.
	 *
	 * @param eventoId el ID del evento.
	 * @return una lista de URLs de las imágenes del evento.
	 */
	 @Query("SELECT ei.url FROM EventoImagen ei WHERE ei.evento.id = :eventoId")
	 List<String> findUrlsByEventoId(@Param("eventoId") Long eventoId);
	    
	 List<EventoImagen> findByEventoId(Long eventoId);
}
