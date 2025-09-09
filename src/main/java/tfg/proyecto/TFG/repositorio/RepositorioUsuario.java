package tfg.proyecto.TFG.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.modelo.Usuario;
@Repository
public interface RepositorioUsuario extends CrudRepository<Usuario, Long> {
	

    Usuario findByEmail(String email);
    
    List<Usuario>findByReportadoTrue();
    
}
