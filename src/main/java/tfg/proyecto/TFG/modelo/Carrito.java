package tfg.proyecto.TFG.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Carrito {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id") //FK del usuario
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "carrito_eventos", //CREA LA TABLA CARRITO_EVENTOS(tabla intermedia)
            joinColumns = @JoinColumn(name = "carrito_id"), //Fk del ID carrito
            inverseJoinColumns = @JoinColumn(name = "evento_id") //FK del ID del evento
    )
    private List<Evento> eventos;
}
