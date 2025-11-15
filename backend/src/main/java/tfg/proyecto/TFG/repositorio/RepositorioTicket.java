package tfg.proyecto.TFG.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.EstadoTicket;
import tfg.proyecto.TFG.modelo.Ticket;
@Repository
public interface RepositorioTicket extends CrudRepository<Ticket, Long>{
	
	List<Ticket> findByUsuarioId(long usuarioId);
	List<Ticket> findByEventoId(long eventoId);
	// Busca un ticket por su contenido QR y que su estado sea VALIDO.
    // Esto asegura que no esté USADO ni ANULADO.
    Optional<Ticket> findByContenidoQRAndEstado(String contenidoQR, EstadoTicket estado);
}
