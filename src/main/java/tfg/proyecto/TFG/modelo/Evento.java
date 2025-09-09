package tfg.proyecto.TFG.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Evento {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id; 
	
	@Column(length = 100)
	private String nombre;
	private String localizacion;
	
	
	@Singular
	@ElementCollection
	@CollectionTable(name = "evento_invitados", 
	joinColumns = @JoinColumn(name = "evento_id"))
	private List<Invitado> invitados;
	@Lob
	private String imagen;
	private LocalDateTime inicioEvento;
	private LocalDateTime finEvento;
	@Lob // Para textos largos
	private String descripcion;
	@Singular
	@Lob
	private List<String> carrusels;
	private double precio;
	@Enumerated(EnumType.STRING)
	private Categoria categoria;

	
}
