package tfg.proyecto.TFG.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DTOCarritoBajada {
	private Long id;
    private Long usuarioId;
    private List<DTOeventoBajada> eventos; 
}
