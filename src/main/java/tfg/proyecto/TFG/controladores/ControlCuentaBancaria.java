package tfg.proyecto.TFG.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tfg.proyecto.TFG.dtos.DTOcuentaBancariaBajada;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaSubidaUpdate;
import tfg.proyecto.TFG.servicios.IServicioCuentaBancaria;

@RestController
@RequestMapping("/tfg/cuentaBancaria/")
public class ControlCuentaBancaria {

	@Autowired
	IServicioCuentaBancaria daoCuentaBancaria;

	@PostMapping("insert")
	public ResponseEntity<DTOcuentaBancariaBajada> insertarCuenta(@RequestBody DTOcuentaBancariaSubida dto) {
		DTOcuentaBancariaBajada cuenta = daoCuentaBancaria.insert(dto);
		return new ResponseEntity<>(cuenta, HttpStatus.OK);
	}
	
	@PutMapping("update")
    public ResponseEntity<DTOcuentaBancariaBajada> actualizarCuenta(@RequestBody DTOcuentaBancariaSubidaUpdate dto) {
        DTOcuentaBancariaBajada cuenta = daoCuentaBancaria.update(dto);
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
    public ResponseEntity<DTOcuentaBancariaBajada> obtenerCuentaPorId(@PathVariable Long id) {
        DTOcuentaBancariaBajada cuenta = daoCuentaBancaria.findById(id);
        if (cuenta != null) {
            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("findAll")
    public ResponseEntity<List<DTOcuentaBancariaBajada>> listarTodasCuentas() {
        List<DTOcuentaBancariaBajada> cuentas = daoCuentaBancaria.listAllCuentasBancarias();
        return new ResponseEntity<>(cuentas, HttpStatus.OK);
    }

}
