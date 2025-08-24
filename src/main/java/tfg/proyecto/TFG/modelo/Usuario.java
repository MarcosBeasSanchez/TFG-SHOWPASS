package tfg.proyecto.TFG.modelo;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id; 
	
	private String nombre; //valores unicos e irrepetibles
	@Column(unique = true)
	private String email; //valores unicos e irrepetibles
	
	private String password; //dto lo devuelve sin la contraseña

	private LocalDate fechaNacimiento;
	
	@Lob
	private String foto;
	
	@Enumerated(EnumType.STRING)
	private Rol rol; //admin o cliente
	
	@OneToOne(optional = true,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private TarjetaBancaria tarjeta;
	
	private Boolean activo;
	
	
	
	
	
	

}
