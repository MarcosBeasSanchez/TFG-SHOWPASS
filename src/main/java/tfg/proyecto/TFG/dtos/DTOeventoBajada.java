package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import tfg.proyecto.TFG.modelo.Categoria;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOeventoBajada {

	private Long id;
	private String nombre;
	private String localizacion;
	private LocalDateTime inicioEvento;
	private LocalDateTime finEvento;
	private List<DTOInvitado> invitados;
	private String imagen;
	private String descripcion;
	@Singular
	private List<String> carrusels;
	private double precio;
	private Categoria categoria;



}
