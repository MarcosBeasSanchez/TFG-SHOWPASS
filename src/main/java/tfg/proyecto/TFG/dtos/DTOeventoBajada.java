package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOeventoBajada {

	private Long id;
	private String nombre;
	private String localizacion;
	private LocalDateTime inicioEvento;
	private LocalDateTime finEvento;
	private List<DTOInvitado> invitados;
	private String imagen;


}
