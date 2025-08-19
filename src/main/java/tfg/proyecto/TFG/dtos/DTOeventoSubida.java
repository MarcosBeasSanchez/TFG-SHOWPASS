package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOeventoSubida {
	
	private String nombre;
	private String localizacion;
	private List<DTOInvitado> invitados;
	private String imagen;
	private LocalDateTime inicioEvento;
	private LocalDateTime finEvento;

}
