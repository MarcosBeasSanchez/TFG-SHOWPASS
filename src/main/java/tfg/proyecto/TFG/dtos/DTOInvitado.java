package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOInvitado {
	private String nombre;
    private String apellidos;
    private String fotoURL;
    private String descripcion;
}
