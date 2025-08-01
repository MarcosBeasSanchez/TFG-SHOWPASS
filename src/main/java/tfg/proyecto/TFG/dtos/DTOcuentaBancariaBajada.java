package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOcuentaBancariaBajada {
	private Long id;
	private String nombrePropietario;
	private String nombreBanco;
	private String IBAN;
	private String BIC;
	private BigDecimal saldo; // saldo disponible

}
