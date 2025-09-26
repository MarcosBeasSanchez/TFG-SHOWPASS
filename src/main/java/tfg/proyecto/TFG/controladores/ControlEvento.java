package tfg.proyecto.TFG.controladores;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tfg.proyecto.TFG.config.FileUtils;
import tfg.proyecto.TFG.dtos.DTOInvitado;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.modelo.Categoria;
import tfg.proyecto.TFG.servicios.IServicioEvento;


@CrossOrigin(origins = "http://localhost:5173") //permite las peticiones desde el front
@RestController
@RequestMapping("/tfg/evento/")
public class ControlEvento {
	
	@Autowired
	IServicioEvento daoEvento;
	
	@PostMapping("insert")
	public ResponseEntity<DTOeventoBajada> insertarEvento(
			@RequestParam String nombre,
	        @RequestParam String localizacion,
	        @RequestParam String inicioEvento,
	        @RequestParam String finEvento,
	        @RequestParam String descripcion,
	        @RequestParam double precio,
	        @RequestParam Categoria categoria,
	        @RequestParam(value = "imagen", required = false) MultipartFile imagen,
	        @RequestParam(value = "carrusels", required = false) List<MultipartFile> carrusels,
	        @RequestParam(value = "invitados") String invitadosJson
	) throws IOException {

	    ObjectMapper mapper = new ObjectMapper();
	    List<DTOInvitado> invitados = mapper.readValue(invitadosJson, new TypeReference<List<DTOInvitado>>() {});

	    DTOeventoSubida dto = new DTOeventoSubida();
	    dto.setNombre(nombre);
	    dto.setLocalizacion(localizacion);
	    dto.setInicioEvento(LocalDateTime.parse(inicioEvento));
	    dto.setFinEvento(LocalDateTime.parse(finEvento));
	    dto.setDescripcion(descripcion);
	    dto.setPrecio(precio);
	    dto.setCategoria(categoria);
	    dto.setInvitados(invitados);

	    if (imagen != null) dto.setImagen(FileUtils.convertirArchivoAString(imagen));
	    if (carrusels != null) {
	        dto.setCarrusels(carrusels.stream()
	            .map(FileUtils::convertirArchivoAString)
	            .toList());
	    }

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
	//trae un evento unicamente escribiendolo de manera exacta
	@GetMapping("findByNombre") 
    public ResponseEntity<DTOeventoBajada> obtenerPorNombre(@RequestParam String nombre) {
        try {
            DTOeventoBajada evento = daoEvento.obtnerPorElNombre(nombre);
            return new ResponseEntity<>(evento, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	//trae un evento unicamente escribiendolo de manera exacta
		@GetMapping("findById") 
	    public ResponseEntity<DTOeventoBajada> obtenerPorId(@RequestParam Long id) {
	        try {
	            DTOeventoBajada evento = daoEvento.obtnerPorElId(id);
	            return new ResponseEntity<>(evento, HttpStatus.OK);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }
	
	@GetMapping("findByCategoria/{categoria}")
	public ResponseEntity<List<DTOeventoBajada>> obtenerPorCategoria(@PathVariable String categoria) {
		 try {
		       
		        Categoria catEnum = Categoria.valueOf(categoria.toUpperCase());
		        List<DTOeventoBajada> eventos = daoEvento.obtenerPorCategoria(catEnum);
		        return new ResponseEntity<>(eventos, HttpStatus.OK);
		    } catch (IllegalArgumentException e) {
		        
		        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		    }
	
	}
	// Busca todos los eventos que contengan la palabra `nombre`, ignorando mayúsculas/minúsculas
	@GetMapping("filterByNombre")
	public ResponseEntity<List<DTOeventoBajada>> busquedaParcialPorNombre(@RequestParam String nombre) {
	    try {
	        List<DTOeventoBajada> eventos = daoEvento.buscarPorNombreConteniendo(nombre);
	        return new ResponseEntity<>(eventos, HttpStatus.OK);
	    } catch (RuntimeException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
}
