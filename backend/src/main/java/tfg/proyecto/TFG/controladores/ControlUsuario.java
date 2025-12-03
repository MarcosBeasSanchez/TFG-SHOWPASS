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


/**
 * Controlador REST para la gestión de usuarios en el sistema.
 *
 * <p>Proporciona endpoints para:
 * <ul>
 *     <li>Registrar nuevos usuarios (mínimo o completo)</li>
 *     <li>Login de usuario con validación de credenciales y generación de JWT</li>
 *     <li>Insertar, actualizar y eliminar usuarios</li>
 *     <li>Obtener información de un usuario por id o listar todos los usuarios</li>
 *     <li>Obtener todos los eventos creados por un usuario</li>
 *     <li>Gestión de usuarios reportados (listar, reportar o quitar reporte)</li>
 *     <li>Validación de sesión actual usando JWT para obtener el perfil del usuario</li>
 * </ul>
 *
 * <p>Todos los endpoints están prefijados con <code>/tfg/usuario/</code> y permiten
 * solicitudes CORS desde cualquier origen (útil para frontends en distintos puertos).
 *
 * <p>La seguridad de sesión se maneja mediante tokens JWT que se envían en el encabezado
 * <code>Authorization: Bearer &lt;token&gt;</code>.
 */
@RestController
@RequestMapping("/tfg/usuario/")
@CrossOrigin(origins = "*") 
public class ControlUsuario {

	@Autowired
	IServicioUsuario daoUsuario;
	
		
		/**
		 * Obtiene el perfil del usuario autenticado usando el token JWT enviado en el encabezado Authorization.
		 *
		 * @param request HttpServletRequest para obtener el encabezado Authorization.
		 * @return DTOusuarioBajada con los datos del usuario si el token es válido; 401 UNAUTHORIZED si el token es inválido, expirado o el usuario está reportado.
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
	
	/**
	 * Registra un nuevo usuario usando los datos mínimos necesarios.
	 *
	 * @param usu DTOusuarioSubidaMinimo con los datos del usuario.
	 * @return DTOusuarioBajada con los datos del usuario registrado y estado HTTP 200 OK.
	 */
	@PostMapping("register")
	public ResponseEntity<DTOusuarioBajada> registrarUsuario(@RequestBody DTOusuarioSubidaMinimo usu) {
		DTOusuarioBajada dtoBajada;
		dtoBajada = daoUsuario.register(usu);
		return new ResponseEntity<DTOusuarioBajada>(dtoBajada, HttpStatus.OK);
	}
	
	/**
	 * Realiza el login del usuario comparando email y contraseña.
	 *
	 * @param dtologin DTOusuarioLogin con email y contraseña.
	 * @return DTOusuarioLoginBajada con token JWT si el login es exitoso; mensaje de error en caso contrario.
	 */
	@PostMapping("login") //post porque http solo admite post y get
	public ResponseEntity<DTOusuarioLoginBajada> loginUsuario(@RequestBody DTOusuarioLogin dtologin){
		DTOusuarioLoginBajada dtoBajada;
		dtoBajada = daoUsuario.login(dtologin);
		return ResponseEntity.ok(dtoBajada);
	}
		
	/**
	 * Inserta un nuevo usuario con todos los datos proporcionados en el DTO.
	 *
	 * @param usu DTOusuarioSubida con los datos completos del usuario.
	 * @return DTOusuarioBajada con los datos del usuario insertado y estado HTTP 200 OK.
	 */
	@PostMapping("insert")
	public ResponseEntity<DTOusuarioBajada> insertarUsuario(@RequestBody DTOusuarioSubida usu) {
		DTOusuarioBajada dtoBajada;
		dtoBajada = daoUsuario.insert(usu);
		return new ResponseEntity<DTOusuarioBajada>(dtoBajada, HttpStatus.OK);

	}

	/**
	 * Actualiza los datos de un usuario existente.
	 *
	 * @param usu DTOusuarioModificarSubida con los nuevos datos.
	 * @param id  ID del usuario a actualizar.
	 * @return DTOusuarioBajada con los datos actualizados y estado HTTP 200 OK; 400 BAD REQUEST si falla la actualización.
	 */
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

	/**
	 * Elimina un usuario por su ID.
	 *
	 * @param id ID del usuario a eliminar.
	 * @return Entero indicando el resultado (1 si se eliminó correctamente, 0 si no existía) y estado HTTP 200 OK o 204 NO CONTENT.
	 */
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
	
	/**
	 * Obtiene un usuario por su ID.
	 *
	 * @param id ID del usuario.
	 * @return DTOusuarioBajada con los datos del usuario o 404 NOT FOUND si no existe.
	 */
	@GetMapping("findById/{id}")
    public ResponseEntity<DTOusuarioBajada> findUsuarioById(@PathVariable Long id) {
        DTOusuarioBajada dto = daoUsuario.findById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

	/**
	 * Lista todos los usuarios registrados en el sistema.
	 *
	 * @return Lista de DTOusuarioBajada y estado HTTP 200 OK.
	 */
    @GetMapping("findAll")
    public ResponseEntity<List<DTOusuarioBajada>> findAllUsuarios() {
        List<DTOusuarioBajada> lista = daoUsuario.findAllUsuarios();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
    
    /**
     * Obtiene todos los eventos creados por un usuario específico.
     *
     * @param id ID del usuario.
     * @return Lista de DTOeventoBajada con los eventos creados y estado HTTP 200 OK.
     */
    @GetMapping("findAllEventosCreados/{id}")
   // El tipo de retorno debe ser List<EventoDTO> 
    public ResponseEntity<List<DTOeventoBajada>> findAllEventosCreadosPorUnUsuario(@PathVariable Long id){
     
     // El servicio ahora devuelve List<EventoDTO>
 	List<DTOeventoBajada> lista = daoUsuario.findAllEventosCreadosPorUnUsuario(id); 
     
     return new ResponseEntity<List<DTOeventoBajada>>(lista, HttpStatus.OK);
    }
    
    /**
     * Obtiene todos los usuarios reportados.
     *
     * @return Lista de DTOUsuarioReportado y estado HTTP 200 OK.
     */
    @GetMapping("findAllReportados")
    public ResponseEntity<List<DTOUsuarioReportado>> getAllReportados() {
        List<DTOUsuarioReportado> lista = daoUsuario.findAllReportados();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    /**
     * Obtiene un usuario reportado por su email.
     *
     * @param email Email del usuario.
     * @return DTOUsuarioReportado si el usuario existe; 404 NOT FOUND si no existe.
     */
    @GetMapping("findByEmail")
    public ResponseEntity<DTOUsuarioReportado> getReportadoByEmail(@RequestParam String email) {
        DTOUsuarioReportado dto = daoUsuario.findByEmail(email);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Cambia el estado de reporte de un usuario (reporta o desreporta) según su email.
     *
     * @param email Email del usuario a reportar o desreportar.
     * @return DTOUsuarioReportado con el estado actualizado; 404 NOT FOUND si el usuario no existe.
     */
    @PutMapping("reportar")
    public ResponseEntity<DTOUsuarioReportado> reportarUsuario(@RequestParam String email) {
        DTOUsuarioReportado dto = daoUsuario.reportarUsuario(email);
        if (dto != null) {
        	return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
    * Quita el reporte de un usuario específico según su email.
    *
    * @param email Email del usuario.
    * @return DTOUsuarioReportado con el estado actualizado; 404 NOT FOUND si el usuario no existe.
    */
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
