package tfg.proyecto.TFG.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOtarjetaBancariaBajada {
    private Long id;
    private String nombreTitular;
    private String nTarjeta;
    private LocalDate fechaCaducidad;
    private String cvv;  
    private BigDecimal saldo;

}
