package tfg.proyecto.TFG.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOeventoBajadaCarrito {
	private Long id;
	private String nombre;
	private String localizacion;
	private LocalDateTime inicioEvento;
	private LocalDateTime finEvento;
	private String imagen;
	private double precio;

}
