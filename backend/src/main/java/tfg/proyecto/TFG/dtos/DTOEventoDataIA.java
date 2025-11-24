package tfg.proyecto.TFG.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tfg.proyecto.TFG.modelo.Categoria;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOEventoDataIA {
	private Long id; 
	private String nombre;
	private Categoria categoria;
	private String descripcion;
	private String localizacion;
	private List<DTOInvitadoBajada> invitados;
	
}
