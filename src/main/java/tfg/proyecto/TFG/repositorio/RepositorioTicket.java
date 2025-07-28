package tfg.proyecto.TFG.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Ticket;
@Repository
public interface RepositorioTicket extends CrudRepository<Ticket, Long>{
	
	List<Ticket> findByUsuarioId(long usuarioId);
	List<Ticket> findByEventoId(long eventoId);
	boolean existsByCodigoQR(String codigoQR);
}
