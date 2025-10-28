package tfg.proyecto.TFG.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tfg.proyecto.TFG.modelo.CarritoItem;
@Repository
public interface RepositorioCarritoItem extends CrudRepository<CarritoItem, Long> {
	@Query("SELECT i FROM CarritoItem i WHERE i.carrito.id = :carritoId")
    List<CarritoItem> findByCarritoId(@Param("carritoId") Long carritoId);

    @Query("SELECT i FROM CarritoItem i WHERE i.carrito.id = :carritoId AND i.evento.id = :eventoId")
    Optional<CarritoItem> findByCarritoAndEvento(@Param("carritoId") Long carritoId,
                                                 @Param("eventoId") Long eventoId);
}
