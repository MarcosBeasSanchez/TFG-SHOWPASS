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

@CrossOrigin(origins = "http://localhost:5173") //permite las peticiones desde el front
@RestController
@RequestMapping("/tfg/cuentaBancaria/")
public class ControlCuentaBancaria {

	@Autowired
	IServicioTarjetaBancaria daoCuentaBancaria;

	@PostMapping("insert")
	public ResponseEntity<DTOtarjetaBancariaBajada> insertarCuenta(@RequestBody DTOtarjetaBancariaSubida dto) {
		DTOtarjetaBancariaBajada cuenta = daoCuentaBancaria.insert(dto);
		return new ResponseEntity<>(cuenta, HttpStatus.OK);
	}
	
	@PutMapping("update")
    public ResponseEntity<DTOtarjetaBancariaBajada> actualizarCuenta(@RequestBody DTOtarjetaBancariaSubidaUpdate dto) {
        DTOtarjetaBancariaBajada cuenta = daoCuentaBancaria.update(dto);
        if (cuenta != null) {
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	@DeleteMapping("delete/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        boolean eliminado = daoCuentaBancaria.deleteById(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("findById/{id}")
    public ResponseEntity<DTOtarjetaBancariaBajada> obtenerCuentaPorId(@PathVariable Long id) {
        DTOtarjetaBancariaBajada cuenta = daoCuentaBancaria.findById(id);
        if (cuenta != null) {
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("findAll")
    public ResponseEntity<List<DTOtarjetaBancariaBajada>> listarTodasCuentas() {
        List<DTOtarjetaBancariaBajada> cuentas = daoCuentaBancaria.listAllCuentasBancarias();
        return new ResponseEntity<>(cuentas, HttpStatus.OK);
    }

}
