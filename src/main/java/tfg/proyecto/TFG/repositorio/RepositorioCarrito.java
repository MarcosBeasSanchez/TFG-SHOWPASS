package tfg.proyecto.TFG.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Carrito;
import tfg.proyecto.TFG.modelo.Usuario;

@Repository
public interface RepositorioCarrito extends CrudRepository<Carrito, Long>{
	@Query("SELECT c FROM Carrito c WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM Carrito c WHERE c.usuario.id = :usuarioId AND c.estado = 'ACTIVO'")
    Optional<Carrito> findActivoByUsuarioId(@Param("usuarioId") Long usuarioId);
}
