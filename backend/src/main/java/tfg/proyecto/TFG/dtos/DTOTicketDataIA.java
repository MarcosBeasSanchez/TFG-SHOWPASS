package tfg.proyecto.TFG.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOTicketDataIA {
	private Long id;
	private Long usuarioId;

    private Long eventoId;
}
