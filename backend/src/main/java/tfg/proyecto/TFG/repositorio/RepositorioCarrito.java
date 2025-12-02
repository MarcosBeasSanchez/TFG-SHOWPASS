package tfg.proyecto.TFG.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Carrito;
import tfg.proyecto.TFG.modelo.EstadoCarrito;


/**
 * Repositorio JPA para la entidad {@link Carrito}. 
 * 
 * Proporciona métodos para acceder y gestionar los carritos asociados a un usuario,
 * incluyendo búsquedas por estado y por usuario específico.
 */
@Repository
public interface RepositorioCarrito extends CrudRepository<Carrito, Long>{
	
  
	  /**
     * Busca el carrito activo de un usuario. 
     * Se considera activo aquel que tenga su estado igual a 'ACTIVO'.
     *
     * @param usuarioId el ID del usuario cuyo carrito activo se desea obtener.
     * @return un {@link Optional} con el carrito activo, si existe.
     */
    @Query("SELECT c FROM Carrito c WHERE c.usuario.id = :usuarioId AND c.estado = 'ACTIVO'")
    Optional<Carrito> findActivoByUsuarioId(@Param("usuarioId") Long usuarioId);

    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, EstadoCarrito estado);
}
