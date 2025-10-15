package tfg.proyecto.TFG.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOInvitadoBajada {
    private Long id;
    private String nombre;
    private String apellidos;
    private String descripcion;
    private String fotoUrl; // URL pública de la imagen
}
