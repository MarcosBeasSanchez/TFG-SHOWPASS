package tfg.proyecto.TFG.dtos;

import lombok.Data;

@Data
public class EventoRecomendadoDTO {
	
	 private Long id;
	 private String nombre;
	 private String localizacion;
	 private String imagen; 
	 private double precio;
}
