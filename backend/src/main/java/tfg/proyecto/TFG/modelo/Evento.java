package tfg.proyecto.TFG.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import tfg.proyecto.TFG.config.TieneId;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Evento implements TieneId{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(length = 100)
    private String nombre;
	@Column(length = 100)
    private String localizacion;

    @Lob
    private String imagen; 

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
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evento_carrusel", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "url_imagen")
    private List<String> imagenesCarruselUrls = new ArrayList<>();;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<Ticket> tickets;
	
}
