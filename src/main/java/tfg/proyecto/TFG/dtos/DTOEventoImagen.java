package tfg.proyecto.TFG.dtos;

import java.time.LocalDateTime;
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
public class DTOEventoImagen {
	private String id;
	private String url;

}
