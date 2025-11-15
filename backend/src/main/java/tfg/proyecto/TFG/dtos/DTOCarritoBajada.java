package tfg.proyecto.TFG.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tfg.proyecto.TFG.modelo.EstadoCarrito;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DTOCarritoBajada {
	 private Long id;
	    private EstadoCarrito estado;
	    private List<DTOCarritoItemBajada> items;
}
