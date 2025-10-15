package tfg.proyecto.TFG.modelo;

import java.time.LocalDate;
import java.util.List;

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
import jakarta.persistence.OneToMany;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(unique = true)
    private String email;
    private String password;
    private LocalDate fechaNacimiento;
    private String foto;

    @Enumerated(EnumType.STRING)
    private Rol rol;  // CLIENTE, VENDEDOR, ADMIN

    private boolean reportado;

    // Relaciones
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tarjeta_id")
    private TarjetaBancaria tarjeta;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Carrito carrito;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    private List<Evento> eventosCreados;
}
