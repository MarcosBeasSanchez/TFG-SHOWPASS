package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOticketBajada {
	private Long id;
	private Long usuarioId; // Fk
	private Long eventoId; // Fk
	private String codigoQR;
	private LocalDateTime fechaCompra; // guardar la fecha actual
	private double precio;
	private String eventoNombre;
    private String eventoImagen;
    private LocalDateTime eventoInicio;
}
