package tfg.proyecto.TFG.servicios;

import java.util.List;

import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaBajada;
import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaSubidaUpdate;

public interface IServicioTarjetaBancaria {
	
	DTOtarjetaBancariaBajada insert(DTOtarjetaBancariaSubida dto);
	DTOtarjetaBancariaBajada update(DTOtarjetaBancariaSubidaUpdate dto);
	boolean deleteById(Long id);
	DTOtarjetaBancariaBajada findById(Long id);
	List<DTOtarjetaBancariaBajada>listAllCuentasBancarias();

}
