package tfg.proyecto.TFG.modelo;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invitado {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(length = 20)
	private String nombre;
	@Column(length = 30)
	private String apellidos;
	@Lob
	private String fotoURL;
	@Column(length = 250) // 250 carracteres maximo
	private String descripcion;
	
	 @ManyToOne
	 @JoinColumn(name = "evento_id")
	 @ToString.Exclude
	 private Evento evento;

}
