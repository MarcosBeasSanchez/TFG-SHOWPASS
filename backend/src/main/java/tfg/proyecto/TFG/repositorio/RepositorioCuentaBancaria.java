package tfg.proyecto.TFG.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.TarjetaBancaria;

/**
 * Repositorio JPA para la entidad {@link TarjetaBancaria}.
 * 
 * Permite consultar las tarjetas bancarias asociadas a un usuario.
 */
@Repository
public interface RepositorioCuentaBancaria extends CrudRepository<TarjetaBancaria, Long>{
		
}
