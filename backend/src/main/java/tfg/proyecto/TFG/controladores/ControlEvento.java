package tfg.proyecto.TFG.controladores;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioInvitado;
import tfg.proyecto.TFG.servicios.IServicioEvento;

/**
 * Controlador REST para la gestión de eventos y sus invitados.
 *
 * <p>Proporciona endpoints para:
 * <ul>
 *     <li>Insertar un evento (con soporte para imágenes y carrusel)</li>
 *     <li>Actualizar eventos (JSON o móvil)</li>
 *     <li>Eliminar eventos o invitados</li>
 *     <li>Obtener eventos por id, nombre, categoría, vendedor o búsqueda parcial</li>
 *     <li>Obtener recomendaciones para un usuario o eventos similares</li>
 * </ul>
 *
 * <p>Todos los endpoints están prefijados con <code>/tfg/evento/</code> y permiten 
 * solicitudes CORS desde cualquier origen.
 */
@CrossOrigin(origins = "*") 
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


	
	/**
     * Inserta un nuevo evento usando parámetros form-data (para compatibilidad con MultipartFile).
     *
     * @param nombre       Nombre del evento
     * @param localizacion Ubicación del evento
     * @param inicioEvento Fecha y hora de inicio en formato ISO
     * @param finEvento    Fecha y hora de fin en formato ISO
     * @param descripcion  Descripción del evento
     * @param precio       Precio del evento
     * @param categoria    Categoría del evento
     * @param vendedorId   ID del vendedor que crea el evento
     * @param imagen       Imagen principal (opcional)
     * @param carrusels    Carrusel de imágenes (opcional)
     * @param invitadosJson JSON con la lista de invitados
     * @return DTO del evento creado o BAD_REQUEST si hay errores
     */
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
	
	
	/**
     * Inserta un evento desde mobile/JSON directamente.
     *
     * @param dto DTO de subida con los datos del evento
     * @return DTO del evento creado
     */
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
	


	/**
     * Obtiene todos los eventos existentes.
     *
     * @return Lista de DTOeventoBajada
     */
	@GetMapping("findAll")
    public ResponseEntity<List<DTOeventoBajada>> obtenerTodosEventos() {
        List<DTOeventoBajada> lista = eventoServicio.obtenerTodosLosEventos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
	
	
	/**
     * Actualiza un evento existente usando JSON.
     *
     * @param id Id del evento a actualizar
     * @param dto DTO con los nuevos datos del evento
     * @return DTO del evento actualizado o NOT_FOUND si no existe
     */
	@PutMapping("update/{id}")
	public ResponseEntity<DTOeventoBajada> actualizarEvento(@PathVariable Long id, @RequestBody DTOeventoSubida dto) {
	    try {
	        // Solo llamamos al servicio con el DTO tal como viene del frontend (con Base64/URLs)
	        DTOeventoBajada actualizado = eventoServicio.actualizarEvento(id, dto);
	        return new ResponseEntity<>(actualizado, HttpStatus.OK);
	        
	    } catch (RuntimeException e) {
	        // ... manejo de errores
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	 /**
     * Actualización específica para móviles.
     *
     * @param id Id del evento
     * @param dto DTO del evento
     * @return DTO actualizado o NOT_FOUND si no existe
     */
	@PutMapping("updateMovil/{id}")
    public ResponseEntity<DTOeventoBajada> actualizarEventoMovil(@PathVariable Long id, @RequestBody DTOeventoSubida dto) {
        try {
            DTOeventoBajada actualizado = eventoServicio.actualizarEventoMovil(id, dto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	 /**
     * Elimina un evento por id.
     *
     * @param id Id del evento
     * @return OK si eliminado, NOT_FOUND si no existe
     */
	@DeleteMapping("delete/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        boolean eliminado = eventoServicio.eliminarEvento(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	/**
     * Obtiene un evento por nombre exacto.
     *
     * @param nombre Nombre del evento
     * @return DTO del evento o NOT_FOUND si no existe
     */
	@GetMapping("findByNombre") 
    public ResponseEntity<DTOeventoBajada> obtenerPorNombre(@RequestParam String nombre) {
        try {
            DTOeventoBajada evento = eventoServicio.obtnerPorElNombre(nombre);
            return new ResponseEntity<>(evento, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	/**
     * Obtiene un evento por id exacto.
     *
     * @param id Id del evento
     * @return DTO del evento o NOT_FOUND
     */
		@GetMapping("findById") 
	    public ResponseEntity<DTOeventoBajada> obtenerPorId(@RequestParam Long id) {
	        try {
	            DTOeventoBajada evento = eventoServicio.obtnerPorElId(id);
	            return new ResponseEntity<>(evento, HttpStatus.OK);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }
	
	/**
	* Obtiene los eventos filtrados por categoría.
	*
	* @param categoria Nombre de la categoría
	* @return Lista de eventos de esa categoría
	*/
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
	
	/**
     * Busca eventos cuyo nombre contenga cierta palabra.
     *
     * @param nombre Subcadena a buscar
     * @return Lista de eventos que coinciden
     */
	@GetMapping("filterByBusqueda")
	public ResponseEntity<List<DTOeventoBajada>> busquedaPorIA(@RequestParam String nombre) {
	    try {
	        List<DTOeventoBajada> eventos = eventoServicio.buscarPorNombreConteniendo(nombre);
	        return new ResponseEntity<>(eventos, HttpStatus.OK);
	    } catch (RuntimeException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	 /**
     * Obtiene todos los eventos de un vendedor específico.
     *
     * @param idVendedor Id del vendedor
     * @return Lista de eventos
     */
	@GetMapping("findByVendedor/{idVendedor}")
	public ResponseEntity<List<DTOeventoBajada>> findByVendedor(@PathVariable Long idVendedor) {
	    return ResponseEntity.ok(eventoServicio.obtenerPorVendedor(idVendedor));
	}
	
	/**
     * Elimina un invitado de un evento.
     *
     * @param id Id del invitado
     * @return OK si eliminado, NOT_FOUND si no existe
     */
	@DeleteMapping("deleteInvitado/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (invitadoDAO.existsById(id)) {
			invitadoDAO.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
	
	
	/**
     * Obtiene eventos recomendados para un usuario.
     *
     * @param userId Id del usuario
     * @return Lista de eventos recomendados
     */
	@GetMapping("/recomendacionUsuario/{userId}")
    public ResponseEntity<List<EventoRecomendadoDTO>> obtenerRecomendacionesUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(eventoServicio.obtenerRecomendacionesUsuario(userId));
    }

	/**
     * Obtiene eventos similares a un evento dado.
     *
     * @param eventoId Id del evento
     * @return Lista de eventos similares
     */
    @GetMapping("/recomendacionEvento/{eventoId}")
    public ResponseEntity<List<EventoRecomendadoDTO>> obtenerSimilaresEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(eventoServicio.obtenerSimilaresEvento(eventoId));
	
    }
}
