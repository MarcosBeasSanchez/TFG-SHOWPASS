package tfg.proyecto.TFG.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.dtos.DTOUsuarioReportado;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioLogin;
import tfg.proyecto.TFG.dtos.DTOusuarioLoginBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioModificarSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubidaMinimo;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.servicios.IServicioUsuario;

@RestController
@RequestMapping("/tfg/usuario/")
@CrossOrigin(origins = "*") // acepta la conexion con http con vite puerto 5173
public class ControlUsuario {

	@Autowired
	IServicioUsuario daoUsuario;
	
		
		/**
		 * Endpoint protegido para validar la sesión actual del usuario usando el JWT.
		 * * @param request El objeto HttpServletRequest para leer el encabezado Authorization.
		 * @return DTOusuarioBajada si el token es válido, o 401 UNAUTHORIZED si es inválido/expirado.
		 */
		@GetMapping("perfil")
		public ResponseEntity<DTOusuarioBajada> getPerfil(HttpServletRequest request) {
		    // 1. Obtener el token del encabezado "Authorization: Bearer <token>"
		    String authHeader = request.getHeader("Authorization");
		    
		    // 2. Llamar al servicio para que gestione la validación del token y la búsqueda del usuario
		    // Nota: Necesitas añadir este método 'validarTokenYObtenerPerfil' a tu interfaz IServicioUsuario
		    
		    DTOusuarioBajada dtoBajada = daoUsuario.validarTokenYObtenerPerfil(authHeader);

		    if (dtoBajada != null) {
		        // Si el servicio devuelve el DTO, el token es válido y la sesión está activa
		        return ResponseEntity.ok(dtoBajada);
		    } else {
		        // Si devuelve null, el token es inválido, expirado o el usuario está reportado (401)
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
		    }
		}
	
	
	@PostMapping("register")
	public ResponseEntity<DTOusuarioBajada> registrarUsuario(@RequestBody DTOusuarioSubidaMinimo usu) {
		DTOusuarioBajada dtoBajada;
		dtoBajada = daoUsuario.register(usu);
		return new ResponseEntity<DTOusuarioBajada>(dtoBajada, HttpStatus.OK);
	}
	
	
	@PostMapping("login") //post porque http solo admite post y get
	public ResponseEntity<DTOusuarioLoginBajada> loginUsuario(@RequestBody DTOusuarioLogin dtologin){
		DTOusuarioLoginBajada dtoBajada;
		dtoBajada = daoUsuario.login(dtologin);
		return ResponseEntity.ok(dtoBajada);
	}
		
	@PostMapping("insert")
	public ResponseEntity<DTOusuarioBajada> insertarUsuario(@RequestBody DTOusuarioSubida usu) {
		DTOusuarioBajada dtoBajada;
		dtoBajada = daoUsuario.insert(usu);
		return new ResponseEntity<DTOusuarioBajada>(dtoBajada, HttpStatus.OK);

	}

	@PostMapping("update/{id}")
	@Modifying
	public ResponseEntity<DTOusuarioBajada> updateUsuario(@RequestBody DTOusuarioModificarSubida usu,@PathVariable Long id) {
		DTOusuarioBajada dtoBajada;
		
		usu.setId(id);
		dtoBajada = daoUsuario.update(usu);
		if (dtoBajada != null) {
			return new ResponseEntity<DTOusuarioBajada>(dtoBajada, HttpStatus.OK);
		} else {
			return new ResponseEntity<DTOusuarioBajada>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("delete/{id}")
	@Transactional
	public ResponseEntity<Integer> deleteUsuario(@PathVariable Long id) {

		Integer n = daoUsuario.deleteById(id);

		if (n != null) {
			return new ResponseEntity<Integer>(n, HttpStatus.OK);
		} else {
			return new ResponseEntity<Integer>(HttpStatus.NO_CONTENT);
		}

	}
	
	@GetMapping("findById/{id}")
    public ResponseEntity<DTOusuarioBajada> findUsuarioById(@PathVariable Long id) {
        DTOusuarioBajada dto = daoUsuario.findById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("findAll")
    public ResponseEntity<List<DTOusuarioBajada>> findAllUsuarios() {
        List<DTOusuarioBajada> lista = daoUsuario.findAllUsuarios();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
    
    @GetMapping("findAllEventosCreados/{id}")
 // El tipo de retorno debe ser List<EventoDTO> 
 public ResponseEntity<List<DTOeventoBajada>> findAllEventosCreadosPorUnUsuario(@PathVariable Long id){
     
     // El servicio ahora devuelve List<EventoDTO>
 	List<DTOeventoBajada> lista = daoUsuario.findAllEventosCreadosPorUnUsuario(id); 
     
     return new ResponseEntity<List<DTOeventoBajada>>(lista, HttpStatus.OK);
 }
    
    @GetMapping("findAllReportados")
    public ResponseEntity<List<DTOUsuarioReportado>> getAllReportados() {
        List<DTOUsuarioReportado> lista = daoUsuario.findAllReportados();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    
    @GetMapping("findByEmail")
    public ResponseEntity<DTOUsuarioReportado> getReportadoByEmail(@RequestParam String email) {
        DTOUsuarioReportado dto = daoUsuario.findByEmail(email);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //Reporta o desReporta un usuario
    @PutMapping("reportar")
    public ResponseEntity<DTOUsuarioReportado> reportarUsuario(@RequestParam String email) {
        DTOUsuarioReportado dto = daoUsuario.reportarUsuario(email);
        if (dto != null) {
        	return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("quitarReport")
    public ResponseEntity<DTOUsuarioReportado> quitarReport(@RequestParam   String email) {
        DTOUsuarioReportado dto = daoUsuario.quitarReport(email);
        if (dto != null) {
        	return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
