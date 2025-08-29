package tfg.proyecto.TFG.modelo;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Ticket {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long usuarioId; // Fk
	private Long eventoId; // Fk
	@Column(columnDefinition = "TEXT")
	private String codigoQR; //generar un codigo QR al comprar
	private LocalDateTime fechaCompra;
	private double precio;
	private String eventoNombre;
    private String eventoImagen;
    private LocalDateTime eventoInicio;

}
