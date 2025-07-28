package tfg.proyecto.TFG.repositorio;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Evento;
@Repository
public interface RepositorioEvento extends CrudRepository<Evento, Long>{
	
	Optional<Evento> findByNombre(String nombre);
}
