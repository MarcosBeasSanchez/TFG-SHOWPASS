package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tfg.proyecto.TFG.modelo.EstadoTicket;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOticketBajada {
	private Long id;
    private String codigoQR;
    private LocalDateTime fechaCompra;
    private double precioPagado;
    private EstadoTicket estado;
    private Long usuarioId;
    private String nombreUsuario;
    
    private Long eventoId;
    private String nombreEvento;
}
