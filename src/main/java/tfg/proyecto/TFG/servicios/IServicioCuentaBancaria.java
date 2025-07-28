package tfg.proyecto.TFG.servicios;

import java.util.List;

import tfg.proyecto.TFG.dtos.DTOcuentaBancariaBajada;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaSubidaUpdate;

public interface IServicioCuentaBancaria {
	
	DTOcuentaBancariaBajada insert(DTOcuentaBancariaSubida dto);
	DTOcuentaBancariaBajada update(DTOcuentaBancariaSubidaUpdate dto);
	boolean deleteById(Long id);
	DTOcuentaBancariaBajada findById(Long id);
	List<DTOcuentaBancariaBajada>listAllCuentasBancarias();

}
