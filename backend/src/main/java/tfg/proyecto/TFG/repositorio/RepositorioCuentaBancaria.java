package tfg.proyecto.TFG.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.TarjetaBancaria;
@Repository
public interface RepositorioCuentaBancaria extends CrudRepository<TarjetaBancaria, Long>{
	@Query("SELECT t FROM TarjetaBancaria t WHERE t.usuario.id = :usuarioId")
    Optional<TarjetaBancaria> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
