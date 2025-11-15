package tfg.proyecto.TFG.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tfg.proyecto.TFG.modelo.Rol;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOusuarioSubidaMinimo {
	
	private String nombre; 
	private String email;
	private String password;
	private LocalDate fechaNacimiento;
	private Rol rol;


}
