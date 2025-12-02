package tfg.proyecto.TFG.repositorio;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.CarritoItem;

/**
 * Repositorio JPA para la gestión de {@link CarritoItem}.
 * 
 * Permite obtener los ítems asociados a un carrito específico, así como
 * buscar un ítem concreto relacionado con un evento dentro de un carrito.
 */
@Repository
public interface RepositorioCarritoItem extends CrudRepository<CarritoItem, Long> {
	

}
