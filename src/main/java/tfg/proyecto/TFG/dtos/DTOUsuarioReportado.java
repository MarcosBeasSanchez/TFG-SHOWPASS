package tfg.proyecto.TFG.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOUsuarioReportado {
	
	private Long id;
	private Boolean reportado;
	private String email;
	private String nombre; 
}
