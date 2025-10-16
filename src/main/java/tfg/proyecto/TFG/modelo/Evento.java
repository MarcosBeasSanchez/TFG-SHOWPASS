package tfg.proyecto.TFG.modelo;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Evento {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(length = 100)
    private String nombre;
	@Column(length = 100)
    private String localizacion;

    @Lob
    private String imagen; // Base64 o URL

    private LocalDateTime inicioEvento;
    private LocalDateTime finEvento;
    @Lob
    private String descripcion;
    private double precio;
    private int aforoMax;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    @ToString.Exclude
    private Usuario vendedor;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Invitado> invitados;
    
    @Singular
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EventoImagen> imagenesCarruselUrls;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Ticket> tickets;

	
}
