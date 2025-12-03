package tfg.proyecto.TFG.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.Usuario;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 * 
 * Proporciona métodos para acceder y gestionar usuarios, incluyendo búsquedas
 * por email, estado de reporte y rol.
 */
@Repository
public interface RepositorioUsuario extends CrudRepository<Usuario, Long> {
	

    Usuario findByEmail(String email);
    
    List<Usuario>findByReportadoTrue();
    
    /**
     * Obtiene todos los usuarios que tienen el rol de 'VENDEDOR'.
     *
     * @return lista de usuarios con rol 'VENDEDOR'
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol = 'VENDEDOR'")
    List<Usuario> findAllVendedores();
}
