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
public class DTOusuarioSubida {
	private String nombre; 
	private String email;
	private String password;
	private LocalDate fechaNacimiento;
	private String foto;
	private Rol rol;
	private DTOtarjetaBancariaSubida cuenta;
	private Boolean activo;
}
