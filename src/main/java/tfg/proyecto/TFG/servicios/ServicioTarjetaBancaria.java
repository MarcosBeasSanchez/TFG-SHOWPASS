package tfg.proyecto.TFG.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaBajada;
import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaSubidaUpdate;
import tfg.proyecto.TFG.modelo.TarjetaBancaria;
import tfg.proyecto.TFG.repositorio.RepositorioCuentaBancaria;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;
@Service
public class ServicioTarjetaBancaria implements IServicioTarjetaBancaria{
	
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
	public DTOtarjetaBancariaBajada insert(DTOtarjetaBancariaSubida dto) {
		DTOtarjetaBancariaBajada dtoBajada;
		TarjetaBancaria cuenta;
		
		cuenta = dtoConverter.map(dto,TarjetaBancaria.class);
		repoCuentaBancaria.save(cuenta);
		dtoBajada = dtoConverter.map(cuenta,DTOtarjetaBancariaBajada.class);
		
		return dtoBajada;
	}
	@Override
	public DTOtarjetaBancariaBajada update(DTOtarjetaBancariaSubidaUpdate dto) {
		DTOtarjetaBancariaBajada dtoBajada;
		TarjetaBancaria cuenta;
		if (repoCuentaBancaria.existsById(dto.getId())) {
			cuenta = dtoConverter.map(dto, TarjetaBancaria.class);
			repoCuentaBancaria.save(cuenta);
			dtoBajada = dtoConverter.map(cuenta, DTOtarjetaBancariaBajada.class);
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
	public DTOtarjetaBancariaBajada findById(Long id) {
		DTOtarjetaBancariaBajada dtobajada;
		TarjetaBancaria cuenta;
		
		if (repoCuentaBancaria.existsById(id)) {
			cuenta = repoCuentaBancaria.findById(id).get();
			dtobajada = dtoConverter.map(cuenta, DTOtarjetaBancariaBajada.class);
		} else {
			dtobajada = null;
		}
		
		return dtobajada;
	}
	@Override
	public List<DTOtarjetaBancariaBajada> listAllCuentasBancarias() {
		return dtoConverter.mapAll((List<TarjetaBancaria>) repoCuentaBancaria.findAll(), DTOtarjetaBancariaBajada.class);
	}


}
