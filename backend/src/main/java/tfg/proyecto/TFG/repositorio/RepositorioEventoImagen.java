package tfg.proyecto.TFG.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.modelo.EventoImagen;

public interface RepositorioEventoImagen extends CrudRepository<EventoImagen, Long> {
	 @Transactional
	    @Modifying
	    @Query("DELETE FROM EventoImagen ei WHERE ei.evento.id = :eventoId")
	    void deleteByEventoId(@Param("eventoId") Long eventoId);

	    @Query("SELECT ei.url FROM EventoImagen ei WHERE ei.evento.id = :eventoId")
	    List<String> findUrlsByEventoId(@Param("eventoId") Long eventoId);
	    
	    List<EventoImagen> findByEventoId(Long eventoId);
}
