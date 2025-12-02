package tfg.proyecto.TFG.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Categoria;
import tfg.proyecto.TFG.modelo.Evento;

/**
 * Repositorio JPA para la entidad {@link Evento}.
 * 
 * Permite realizar operaciones de consulta relacionadas con eventos,
 * incluyendo búsquedas por nombre, categoría y vendedor.
 */
@Repository
public interface RepositorioEvento extends CrudRepository<Evento, Long>{
	

	Optional<Evento> findByNombre(String nombre);
	List<Evento> findByCategoria(Categoria categoria);
	
	/**
     * Busca eventos cuyo nombre contenga la cadena proporcionada,
     * ignorando mayúsculas y minúsculas.
     *
     * @param nombre parte del nombre a buscar.
     * @return lista de eventos cuyo nombre contiene la cadena indicada.
     */    
	List<Evento> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Obtiene todos los eventos asociados a un vendedor específico.
     *
     * @param vendedorId el ID del vendedor cuyos eventos se desean consultar.
     * @return lista de eventos publicados por el vendedor.
     */
     @Query("SELECT e FROM Evento e WHERE e.vendedor.id = :vendedorId")
     List<Evento> findByVendedorId(@Param("vendedorId") Long vendedorId);
}
