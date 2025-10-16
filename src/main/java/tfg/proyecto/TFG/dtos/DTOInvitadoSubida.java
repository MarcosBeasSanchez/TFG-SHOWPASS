package tfg.proyecto.TFG.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOInvitadoSubida {
	private String nombre;
    private String apellidos;
    private String fotoURL;
    private String descripcion; 
}