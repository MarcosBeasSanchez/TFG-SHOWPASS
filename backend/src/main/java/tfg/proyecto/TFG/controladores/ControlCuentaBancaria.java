package tfg.proyecto.TFG.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaBajada;
import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaSubidaUpdate;
import tfg.proyecto.TFG.servicios.IServicioTarjetaBancaria;

/**
 * Controlador REST para la gestión de cuentas bancarias / tarjetas de usuario.
 *
 * <p>Proporciona endpoints para:
 * <ul>
 *     <li>Insertar una nueva cuenta bancaria</li>
 *     <li>Actualizar una cuenta existente</li>
 *     <li>Eliminar una cuenta por id</li>
 *     <li>Obtener una cuenta por id</li>
 *     <li>Listar todas las cuentas</li>
 * </ul>
 *
 * <p>Todos los endpoints están prefijados con <code>/tfg/cuentaBancaria/</code> y permiten 
 * solicitudes CORS desde cualquier origen.
 */
@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/tfg/cuentaBancaria/")
public class ControlCuentaBancaria {

	@Autowired
	IServicioTarjetaBancaria daoCuentaBancaria;


    /**
     * Inserta una nueva cuenta bancaria (tarjeta) en el sistema.
     *
     * @param dto DTO de subida con los datos de la tarjeta
     * @return DTO de bajada con la tarjeta creada
     */
	@PostMapping("insert")
	public ResponseEntity<DTOtarjetaBancariaBajada> insertarCuenta(@RequestBody DTOtarjetaBancariaSubida dto) {
		DTOtarjetaBancariaBajada cuenta = daoCuentaBancaria.insert(dto);
		return new ResponseEntity<>(cuenta, HttpStatus.OK);
	}
	
	/**
     * Actualiza una cuenta bancaria existente.
     *
     * @param dto DTO de actualización con los datos nuevos
     * @return DTO de bajada actualizado o NOT_FOUND si no existe
     */
	@PutMapping("update")
    public ResponseEntity<DTOtarjetaBancariaBajada> actualizarCuenta(@RequestBody DTOtarjetaBancariaSubidaUpdate dto) {
        DTOtarjetaBancariaBajada cuenta = daoCuentaBancaria.update(dto);
        if (cuenta != null) {
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	/**
     * Elimina una cuenta bancaria por su ID.
     *
     * @param id ID de la cuenta a eliminar
     * @return OK si se eliminó, NOT_FOUND si no existe
     */
	@DeleteMapping("delete/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        boolean eliminado = daoCuentaBancaria.deleteById(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

	/**
     * Obtiene una cuenta bancaria por su ID.
     *
     * @param id ID de la cuenta
     * @return DTO de bajada con la cuenta o NOT_FOUND si no existe
     */
    @GetMapping("findById/{id}")
    public ResponseEntity<DTOtarjetaBancariaBajada> obtenerCuentaPorId(@PathVariable Long id) {
        DTOtarjetaBancariaBajada cuenta = daoCuentaBancaria.findById(id);
        if (cuenta != null) {
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Lista todas las cuentas bancarias registradas.
     *
     * @return Lista de DTOs de bajada de todas las cuentas
     */
    @GetMapping("findAll")
    public ResponseEntity<List<DTOtarjetaBancariaBajada>> listarTodasCuentas() {
        List<DTOtarjetaBancariaBajada> cuentas = daoCuentaBancaria.listAllCuentasBancarias();
        return new ResponseEntity<>(cuentas, HttpStatus.OK);
    }

}
