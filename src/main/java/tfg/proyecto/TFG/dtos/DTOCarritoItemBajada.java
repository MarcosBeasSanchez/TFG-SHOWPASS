package tfg.proyecto.TFG.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCarritoItemBajada {
    private Long id;
    private int cantidad;
    private double precioUnitario;
    private Long eventoId;
    private String nombreEvento;
}
