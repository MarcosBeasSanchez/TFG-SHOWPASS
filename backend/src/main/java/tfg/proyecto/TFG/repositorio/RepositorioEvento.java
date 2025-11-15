package tfg.proyecto.TFG.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Categoria;
import tfg.proyecto.TFG.modelo.Evento;
@Repository
public interface RepositorioEvento extends CrudRepository<Evento, Long>{
	
	Optional<Evento> findByNombre(String nombre);
	List<Evento> findByCategoria(Categoria categoria);
	// Busca todos los eventos cuyo nombre contenga la cadena 'nombre', ignorando mayúsculas/minúsculas
    List<Evento> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT e FROM Evento e WHERE e.vendedor.id = :vendedorId")
    List<Evento> findByVendedorId(@Param("vendedorId") Long vendedorId);
}
