package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
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
public class DTOusuarioModificarSubida {
	private Long id;
	private String nombre; 
	private String email;
	private String password;
	private LocalDate fechaNacimiento;
	private Rol rol;
	private DTOcuentaBancariaSubida cuenta;
	private Boolean activo;
}
