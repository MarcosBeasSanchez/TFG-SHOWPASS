package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DTOticketSubida {
	@EqualsAndHashCode.Include
	private Long usuarioId; // Fk
	@EqualsAndHashCode.Include
	private Long eventoId; // Fk
	//private String codigoQR;
	//private LocalDateTime fechaCompra; //en el servicio guardar la fecha actual
}
