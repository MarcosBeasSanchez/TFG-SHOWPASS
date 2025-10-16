package tfg.proyecto.TFG.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import tfg.proyecto.TFG.modelo.Categoria;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOeventoBajada {

	private Long id;
    private String nombre;
    private String localizacion;
    private LocalDateTime inicioEvento;
    private LocalDateTime finEvento;
    private String descripcion;
    private double precio;
    private int aforo;
    private Categoria categoria;

    private String imagenPrincipalUrl;
    @Singular
    private List<String> imagenesCarruselUrls;

    private List<DTOInvitadoBajada> invitados;
    
    private Long vendedorId;



}
