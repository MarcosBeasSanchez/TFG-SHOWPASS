package tfg.proyecto.TFG.controladores;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tfg.proyecto.TFG.config.FileUtils;
import tfg.proyecto.TFG.dtos.DTOInvitadoSubida;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.dtos.EventoRecomendadoDTO;
import tfg.proyecto.TFG.modelo.Categoria;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioInvitado;
import tfg.proyecto.TFG.servicios.IServicioEvento;


@CrossOrigin(origins = "*") //permite las peticiones desde el front
@RestController
@RequestMapping("/tfg/evento/")
public class ControlEvento {
	
	@Autowired
	IServicioEvento eventoServicio;
	
	@Autowired 
	RepositorioInvitado invitadoDAO;
	
	
	@Autowired
	RepositorioEvento eventoDAO;
	
	private final RestTemplate restTemplate = new RestTemplate();


	
	//Con parametros porque multifilePart y JSOn Da error
	@PostMapping("insert")
	public ResponseEntity<DTOeventoBajada> insertarEvento(
	        @RequestParam String nombre,
	        @RequestParam String localizacion,
	        @RequestParam String inicioEvento,
	        @RequestParam String finEvento,
	        @RequestParam String descripcion,
	        @RequestParam double precio,
	        @RequestParam String categoria,
	        @RequestParam Long vendedorId, 
	        @RequestParam(value = "imagen", required = false) MultipartFile imagen,
	        @RequestParam(value = "carrusels", required = false) MultipartFile[] carrusels,
	        @RequestParam(value = "invitados") String invitadosJson
	) {
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        List<DTOInvitadoSubida> invitados = mapper.readValue(invitadosJson, new TypeReference<>() {});

	        DTOeventoSubida dto = new DTOeventoSubida();
	        dto.setNombre(nombre);
	        dto.setLocalizacion(localizacion);
	        dto.setInicioEvento(LocalDateTime.parse(inicioEvento));
	        dto.setFinEvento(LocalDateTime.parse(finEvento));
	        dto.setDescripcion(descripcion);
	        dto.setPrecio(precio);
	        dto.setCategoria(Categoria.valueOf(categoria.toUpperCase()));
	        dto.setInvitados(invitados);
	        dto.setVendedorId(vendedorId);

	        // Imagen principal
	        if (imagen != null && !imagen.isEmpty()) {
	            dto.setImagen(FileUtils.convertirArchivoAString(imagen));
	        }

	        // Carrusel
	        if (carrusels != null && carrusels.length > 0) {
	            List<String> carruselBase64 = Arrays.stream(carrusels)
	                    .filter(file -> file != null && !file.isEmpty())
	                    .map(file -> {
	                        try {
	                            return FileUtils.convertirArchivoAString(file);
	                        } catch (IOException e) {
	                            throw new RuntimeException("Error al procesar imagen del carrusel", e);
	                        }
	                    })
	                    .toList();
	            dto.setImagenesCarruselUrls(carruselBase64);
	        }

	        DTOeventoBajada evento = eventoServicio.insert(dto);
	        return new ResponseEntity<>(evento, HttpStatus.CREATED);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
	
	@PostMapping("insert/mobile")
	public ResponseEntity<DTOeventoBajada> insertarEventoMobile(
	        @RequestBody DTOeventoSubida dto
	) {
	    try {
	        DTOeventoBajada evento = eventoServicio.insert(dto);
	        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	    }
	}
	

	
	@GetMapping("findAll")
    public ResponseEntity<List<DTOeventoBajada>> obtenerTodosEventos() {
        List<DTOeventoBajada> lista = eventoServicio.obtenerTodosLosEventos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
	
	
	@PutMapping("update/{id}")
    public ResponseEntity<DTOeventoBajada> actualizarEvento(@PathVariable Long id, @RequestBody DTOeventoSubida dto) {
        try {
            DTOeventoBajada actualizado = eventoServicio.actualizarEvento(id, dto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	@PutMapping("updateMovil/{id}")
    public ResponseEntity<DTOeventoBajada> actualizarEventoMovil(@PathVariable Long id, @RequestBody DTOeventoSubida dto) {
        try {
            DTOeventoBajada actualizado = eventoServicio.actualizarEventoMovil(id, dto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	
	@DeleteMapping("delete/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        boolean eliminado = eventoServicio.eliminarEvento(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	//trae un evento unicamente escribiendolo de manera exacta
	@GetMapping("findByNombre") 
    public ResponseEntity<List<DTOeventoBajada>> obtenerPorNombre(@RequestParam String nombre) {
		 try {
		        List<DTOeventoBajada> eventos = eventoServicio.buscarPorNombreConteniendo(nombre);
		        return ResponseEntity.ok(eventos);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		    }
    }
	
	//trae un evento unicamente escribiendolo de manera exacta
		@GetMapping("findById") 
	    public ResponseEntity<DTOeventoBajada> obtenerPorId(@RequestParam Long id) {
	        try {
	            DTOeventoBajada evento = eventoServicio.obtnerPorElId(id);
	            return new ResponseEntity<>(evento, HttpStatus.OK);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }
	
	@GetMapping("findByCategoria/{categoria}")
	public ResponseEntity<List<DTOeventoBajada>> obtenerPorCategoria(@PathVariable String categoria) {
		 try {
		       
		        Categoria catEnum = Categoria.valueOf(categoria.toUpperCase());
		        List<DTOeventoBajada> eventos = eventoServicio.obtenerPorCategoria(catEnum);
		        return new ResponseEntity<>(eventos, HttpStatus.OK);
		    } catch (IllegalArgumentException e) {
		        
		        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		    }
	
	}
	 
	
	@GetMapping("findByVendedor/{idVendedor}")
	public ResponseEntity<List<DTOeventoBajada>> findByVendedor(@PathVariable Long idVendedor) {
	    return ResponseEntity.ok(eventoServicio.obtenerPorVendedor(idVendedor));
	}
	
	@DeleteMapping("deleteInvitado/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (invitadoDAO.existsById(id)) {
			invitadoDAO.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
	
	
	
	@GetMapping("/recomendacionUsuario/{userId}")
    public ResponseEntity<List<EventoRecomendadoDTO>> obtenerRecomendacionesUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(eventoServicio.obtenerRecomendacionesUsuario(userId));
    }

    @GetMapping("/recomendacionEvento/{eventoId}")
    public ResponseEntity<List<EventoRecomendadoDTO>> obtenerSimilaresEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(eventoServicio.obtenerSimilaresEvento(eventoId));
	
    }
}
