package tfg.proyecto.TFG.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioModificarSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.servicios.IServicioUsuario;

@RestController
@RequestMapping("/tfg/usuario/")
public class ControlUsuario {

	@Autowired
	IServicioUsuario daousuario;

	@PostMapping("insert")
	public ResponseEntity<DTOusuarioBajada> insertarUsuario(@RequestBody DTOusuarioSubida usu) {
		DTOusuarioBajada dtoBajada;
		dtoBajada = daousuario.insert(usu);
		return new ResponseEntity<DTOusuarioBajada>(dtoBajada, HttpStatus.OK);

	}

	@PutMapping("update")
	@Modifying
	public ResponseEntity<DTOusuarioBajada> updateUsuario(@RequestBody DTOusuarioModificarSubida usu) {
		DTOusuarioBajada dtoBajada;
		dtoBajada = daousuario.update(usu);
		if (dtoBajada != null) {
			return new ResponseEntity<DTOusuarioBajada>(dtoBajada, HttpStatus.OK);
		} else {
			return new ResponseEntity<DTOusuarioBajada>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("delete/{id}")
	@Modifying
	@Transactional
	public ResponseEntity<Integer> deleteUsuario(@PathVariable Long id) {

		Integer n = daousuario.deleteById(id);

		if (n != null) {
			return new ResponseEntity<Integer>(n, HttpStatus.OK);
		} else {
			return new ResponseEntity<Integer>(HttpStatus.NOT_FOUND);
		}

	}
	
	@GetMapping("findById/{id}")
    public ResponseEntity<DTOusuarioBajada> findUsuarioById(@PathVariable Long id) {
        DTOusuarioBajada dto = daousuario.findById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("findAll")
    public ResponseEntity<List<DTOusuarioBajada>> findAllUsuarios() {
        List<DTOusuarioBajada> lista = daousuario.findAllUsuarios();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
}
