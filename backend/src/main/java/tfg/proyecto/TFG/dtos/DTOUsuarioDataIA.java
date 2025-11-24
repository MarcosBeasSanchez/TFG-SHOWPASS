package tfg.proyecto.TFG.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOUsuarioDataIA {
	private Long id;
	private String nombre;
	private List<DTOTicketDataIA> tickets;
}
