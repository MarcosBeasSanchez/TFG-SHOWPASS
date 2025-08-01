package tfg.proyecto.TFG.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaBajada;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaSubidaUpdate;
import tfg.proyecto.TFG.modelo.CuentaBancaria;
import tfg.proyecto.TFG.repositorio.RepositorioCuentaBancaria;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;
@Service
public class ServicioCuentaBancaria implements IServicioCuentaBancaria{
	
	@Autowired
	RepositorioUsuario repoUsuario;
	@Autowired
	RepositorioCuentaBancaria repoCuentaBancaria;
	@Autowired
	RepositorioEvento repoEvento;
	@Autowired
	RepositorioTicket repoTicket;
	@Autowired
	DtoConverter dtoConverter;
	
	@Override
	public DTOcuentaBancariaBajada insert(DTOcuentaBancariaSubida dto) {
		DTOcuentaBancariaBajada dtoBajada;
		CuentaBancaria cuenta;
		
		cuenta = dtoConverter.map(dto,CuentaBancaria.class);
		repoCuentaBancaria.save(cuenta);
		dtoBajada = dtoConverter.map(cuenta,DTOcuentaBancariaBajada.class);
		
		return dtoBajada;
	}
	@Override
	public DTOcuentaBancariaBajada update(DTOcuentaBancariaSubidaUpdate dto) {
		DTOcuentaBancariaBajada dtoBajada;
		CuentaBancaria cuenta;
		if (repoCuentaBancaria.existsById(dto.getId())) {
			cuenta = dtoConverter.map(dto, CuentaBancaria.class);
			repoCuentaBancaria.save(cuenta);
			dtoBajada = dtoConverter.map(cuenta, DTOcuentaBancariaBajada.class);
		} else {
			dtoBajada = null;
		}
		return dtoBajada;
	}
	@Override
	public boolean deleteById (Long id) {
		boolean exito;
		if (repoCuentaBancaria.existsById(id)) {
			repoCuentaBancaria.deleteById(id); //cuidado al eliminar
			exito=true;
		} else {
			exito=false;
		}
		return exito;
	}
	@Override
	public DTOcuentaBancariaBajada findById(Long id) {
		DTOcuentaBancariaBajada dtobajada;
		CuentaBancaria cuenta;
		
		if (repoCuentaBancaria.existsById(id)) {
			cuenta = repoCuentaBancaria.findById(id).get();
			dtobajada = dtoConverter.map(cuenta, DTOcuentaBancariaBajada.class);
		} else {
			dtobajada = null;
		}
		
		return dtobajada;
	}
	@Override
	public List<DTOcuentaBancariaBajada> listAllCuentasBancarias() {
		return dtoConverter.mapAll((List<CuentaBancaria>) repoCuentaBancaria.findAll(), DTOcuentaBancariaBajada.class);
	}


}
