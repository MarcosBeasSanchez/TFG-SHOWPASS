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

/**
 * Servicio encargado de gestionar las cuentas bancarias (tarjetas) de los usuarios.
 * 
 * <p>Proporciona operaciones CRUD básicas sobre la entidad {@link TarjetaBancaria}:</p>
 * <ul>
 *     <li>Insertar nuevas tarjetas.</li>
 *     <li>Actualizar tarjetas existentes.</li>
 *     <li>Eliminar tarjetas por ID.</li>
 *     <li>Buscar tarjetas por ID.</li>
 *     <li>Listar todas las tarjetas bancarias.</li>
 * </ul>
 */
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
	
	/**
     * Inserta una nueva tarjeta bancaria en la base de datos.
     *
     * @param dto DTO de subida con los datos de la tarjeta
     * @return DTO de bajada con los datos guardados
     */
	@Override
	public DTOtarjetaBancariaBajada insert(DTOtarjetaBancariaSubida dto) {
		DTOtarjetaBancariaBajada dtoBajada;
		TarjetaBancaria cuenta;
		
		cuenta = dtoConverter.map(dto,TarjetaBancaria.class);
		repoCuentaBancaria.save(cuenta);
		dtoBajada = dtoConverter.map(cuenta,DTOtarjetaBancariaBajada.class);
		
		return dtoBajada;
	}
	

    /**
     * Actualiza una tarjeta bancaria existente.
     *
     * @param dto DTO con datos actualizados y el ID de la tarjeta
     * @return DTO de bajada con los datos actualizados, o null si no existe
     */
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
	
	/**
     * Elimina una tarjeta bancaria por su ID.
     *
     * @param id ID de la tarjeta a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
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
	
	 /**
     * Obtiene una tarjeta bancaria por su ID.
     *
     * @param id ID de la tarjeta
     * @return DTO de bajada con los datos de la tarjeta, o null si no existe
     */
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
	
	 /**
     * Lista todas las cuentas bancarias registradas.
     *
     * @return lista de DTOs de bajada con todas las tarjetas
     */
	@Override
	public List<DTOtarjetaBancariaBajada> listAllCuentasBancarias() {
		return dtoConverter.mapAll((List<TarjetaBancaria>) repoCuentaBancaria.findAll(), DTOtarjetaBancariaBajada.class);
	}
}
