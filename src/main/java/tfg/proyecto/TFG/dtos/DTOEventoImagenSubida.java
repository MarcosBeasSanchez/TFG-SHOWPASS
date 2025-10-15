package tfg.proyecto.TFG.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOEventoImagenSubida {
    private String imagenBase64; // Imagen en Base64 o URL

}
