package tfg.proyecto.TFG.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.TarjetaBancaria;
@Repository
public interface RepositorioCuentaBancaria extends CrudRepository<TarjetaBancaria, Long>{

}
