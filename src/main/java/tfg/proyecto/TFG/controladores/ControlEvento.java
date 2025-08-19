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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.servicios.IServicioEvento;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tfg/evento/")
public class ControlEvento {
	
	@Autowired
	IServicioEvento daoEvento;
	
	@PostMapping("insert")
    public ResponseEntity<DTOeventoBajada> insertarEvento(@RequestBody DTOeventoSubida dto) {
        DTOeventoBajada evento = daoEvento.insert(dto);
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }
	
	@GetMapping("findAll")
    public ResponseEntity<List<DTOeventoBajada>> obtenerTodosEventos() {
        List<DTOeventoBajada> lista = daoEvento.obtenerTodosLosEventos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
	
	@PutMapping("update/{id}")
    public ResponseEntity<DTOeventoBajada> actualizarEvento(@PathVariable Long id, @RequestBody DTOeventoSubida dto) {
        try {
            DTOeventoBajada actualizado = daoEvento.actualizarEvento(id, dto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	@DeleteMapping("delete/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        boolean eliminado = daoEvento.eliminarEvento(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	@GetMapping("findByNombre") //parametro
    public ResponseEntity<DTOeventoBajada> obtenerPorNombre(@RequestParam String nombre) {
        try {
            DTOeventoBajada evento = daoEvento.obtnerPorElNombre(nombre);
            return new ResponseEntity<>(evento, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	

}
