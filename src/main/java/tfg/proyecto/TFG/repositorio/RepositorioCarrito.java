package tfg.proyecto.TFG.repositorio;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Carrito;
import tfg.proyecto.TFG.modelo.Usuario;

@Repository
public interface RepositorioCarrito extends CrudRepository<Carrito, Long>{
    Optional<Carrito> findByUsuario(Usuario usuario);
    Optional<Carrito> findByUsuarioId(Long usuarioId); 
}
