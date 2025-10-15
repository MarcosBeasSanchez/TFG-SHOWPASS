package tfg.proyecto.TFG.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import tfg.proyecto.TFG.modelo.Categoria;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOeventoSubida {
		
		private Long id;
	 	private String nombre;
	    private String localizacion;
	    private LocalDateTime inicioEvento;
	    private LocalDateTime finEvento;
	    private String descripcion;
	    private double precio;
	    private int aforo;
	    private Categoria categoria;


	    private String imagen; 
	    
	    @Singular
	    private List<String> imagenesCarrusels;


	    private List<DTOInvitadoSubida> invitados;
	    private Long vendedorId;

          

}
