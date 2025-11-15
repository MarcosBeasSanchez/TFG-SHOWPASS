package tfg.proyecto.TFG.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.modelo.Invitado;

public interface RepositorioInvitado extends CrudRepository<Invitado, Long> {
	List<Invitado> findByEventoId(Long eventoId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Invitado i WHERE i.evento.id = :eventoId")
    void deleteByEventoId(@Param("eventoId") Long eventoId);
}
