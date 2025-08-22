package tfg.proyecto.TFG.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tfg.proyecto.TFG.modelo.Rol;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOusuarioLoginBajada {
	
	boolean exito;
	String mensaje;
	String token; //token JWT falta por implementarlo,
	DTOusuarioBajada dtousuarioBajada;

}
