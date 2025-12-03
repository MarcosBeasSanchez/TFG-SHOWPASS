package tfg.proyecto.TFG.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.EstadoTicket;
import tfg.proyecto.TFG.modelo.Ticket;

/**
 * Repositorio JPA para la entidad {@link Ticket}.
 * 
 * Proporciona métodos para consultar y gestionar tickets asociados a usuarios y eventos,
 * así como para verificar tickets por su contenido QR y estado.
 */
@Repository
public interface RepositorioTicket extends CrudRepository<Ticket, Long>{
	
	List<Ticket> findByUsuarioId(long usuarioId);
	List<Ticket> findByEventoId(long eventoId);
	/**
     * Busca un ticket por su contenido QR y su estado.
     * Esto asegura que solo se obtengan tickets que coincidan con el estado especificado,
     * por ejemplo, {@code VALIDO}, evitando tickets {@code USADO} o {@code ANULADO}.
     *
     * @param contenidoQR el contenido del código QR del ticket
     * @param estado el estado del ticket a buscar
     * @return un {@link Optional} con el ticket encontrado, si existe
     */
	Optional<Ticket> findByContenidoQRAndEstado(String contenidoQR, EstadoTicket estado);
}
