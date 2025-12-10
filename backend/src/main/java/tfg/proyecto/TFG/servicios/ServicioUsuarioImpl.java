package tfg.proyecto.TFG.servicios;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.config.JwtUtil;
import tfg.proyecto.TFG.dtos.DTOUsuarioReportado;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioLogin;
import tfg.proyecto.TFG.dtos.DTOusuarioLoginBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioModificarSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubidaMinimo;
import tfg.proyecto.TFG.modelo.Carrito;
import tfg.proyecto.TFG.modelo.EstadoCarrito;
import tfg.proyecto.TFG.modelo.Evento;
import tfg.proyecto.TFG.modelo.Rol;
import tfg.proyecto.TFG.modelo.TarjetaBancaria;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioCuentaBancaria;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;


/**
 * Servicio que gestiona la lógica de negocio relacionada con los usuarios.
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 *     <li>Crear, modificar y eliminar usuarios.</li>
 *     <li>Registrar usuarios con datos mínimos o completos.</li>
 *     <li>Login y validación de credenciales mediante JWT.</li>
 *     <li>Gestión de foto de perfil (Base64 o URL).</li>
 *     <li>Gestión de usuarios reportados y desbloqueo.</li>
 *     <li>Obtener eventos creados por un usuario.</li>
 * </ul>
 */
@Service
public class ServicioUsuarioImpl implements IServicioUsuario {

	@Autowired
	RepositorioUsuario repoUsuario;
	@Autowired
	RepositorioCuentaBancaria repoCuentaBancaria;
	@Autowired
	RepositorioEvento repoEvento;
	@Autowired
	RepositorioTicket repoTicket;
	@Autowired
	DtoConverter dtoConverter;
	@Autowired
	ServicioImagenImpl servicioImagen;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Si el rol no se especifica, se asigna CLIENTE por defecto.
     * La contraseña se codifica con BCrypt y se inicializa el carrito.
     *
     * @param usuarioDto DTO con los datos del usuario a crear
     * @return DTO con los datos del usuario guardado
     */
	@Override
	public DTOusuarioBajada insert(DTOusuarioSubida usuarioDto) {
		// Rol por defecto
		if (usuarioDto.getRol() == null)
			usuarioDto.setRol(Rol.CLIENTE);

		Usuario usuario = dtoConverter.map(usuarioDto, Usuario.class);

		// Foto opcional en Base64
		try {
			if (usuarioDto.getFoto() != null && usuarioDto.getFoto().length() > 200) {
				String ruta = servicioImagen.guardarImagenBase64(usuarioDto.getFoto(), "usuarios");
				usuario.setFoto(ruta);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error guardando foto de usuario", e);
		}

		// Hash de contraseña y creación de carrito
		if (usuario.getPassword() != null) {
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

			Carrito carrito = new Carrito();
			carrito.setUsuario(usuario);
			carrito.setEstado(EstadoCarrito.ACTIVO);
			usuario.setCarrito(carrito);
		}

		repoUsuario.save(usuario);
		return dtoConverter.map(usuario, DTOusuarioBajada.class);
	}

    /**
     * Actualiza los datos de un usuario existente.
     * Solo actualiza campos que vengan no nulos en el DTO.
     *
     * @param usuarioDto DTO con datos actualizados
     * @return DTO con datos actualizados
     */
	@Override
	public DTOusuarioBajada update(DTOusuarioModificarSubida usuarioDto) {
		Usuario u = repoUsuario.findById(usuarioDto.getId())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		// Campos editables
		u.setNombre(usuarioDto.getNombre());
		u.setEmail(usuarioDto.getEmail());
		u.setFechaNacimiento(usuarioDto.getFechaNacimiento());

		if (usuarioDto.getRol() != null)
			u.setRol(usuarioDto.getRol());

		// Foto (si viene base64 larga) o mantener existente si llega null/vacía
		try {
			String fotoIn = usuarioDto.getFoto();
			if (fotoIn != null && fotoIn.length() > 200) {
				String ruta = servicioImagen.guardarImagenBase64(fotoIn, "usuarios");
				u.setFoto(ruta);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error guardando foto de usuario", e);
		}

		// Password solo si llega una nueva no vacía
		if (usuarioDto.getPassword() != null && !usuarioDto.getPassword().isBlank()) {
			u.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
		}

		// Tarjeta (si tu DTO trae sub-objeto)
		if (usuarioDto.getCuenta() != null) {
			if (u.getTarjeta() == null)
				u.setTarjeta(new TarjetaBancaria());
			u.getTarjeta().setNombreTitular(usuarioDto.getCuenta().getNombreTitular());
			u.getTarjeta().setNTarjeta(usuarioDto.getCuenta().getNTarjeta());
			u.getTarjeta().setFechaCaducidad(usuarioDto.getCuenta().getFechaCaducidad());
			u.getTarjeta().setCvv(usuarioDto.getCuenta().getCvv());
			u.getTarjeta().setSaldo(usuarioDto.getCuenta().getSaldo());
		}

		repoUsuario.save(u);
		return dtoConverter.map(u, DTOusuarioBajada.class);
	}

	 /**
     * Elimina un usuario por ID.
     *
     * @param id ID del usuario
     * @return 1 si se eliminó, 0 si no existía
     */
	@Override
	public Integer deleteById(Long id) {
		if (!repoUsuario.existsById(id))
			return 0;
		repoUsuario.deleteById(id);
		return 1;
	}

	/**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario
     * @return DTO con los datos del usuario
     */
	@Override
	public DTOusuarioBajada findById(Long id) {
		Usuario u = repoUsuario.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		return dtoConverter.map(u, DTOusuarioBajada.class);
	}

	   /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de DTOs de usuarios
     */
	@Override
	public List<DTOusuarioBajada> findAllUsuarios() {
		return dtoConverter.mapAll((List<Usuario>) repoUsuario.findAll(), DTOusuarioBajada.class);

	}
	
	 /**
     * Valida un token JWT y obtiene el perfil del usuario asociado.
     *
     * @param authHeader Encabezado de autorización con el token Bearer
     * @return DTO del usuario si el token es válido y el usuario no está reportado; null en caso contrario
     */
	@Override
    public DTOusuarioBajada validarTokenYObtenerPerfil(String authHeader) {
        
        // Verificar el formato del encabezado
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Validacion Fallida: No se proporcionó Token o el formato es incorrecto.");
            return null; // Rechazo inmediato
        }
        
        // Extraer el Token JWT
        String token = authHeader.substring(7);
        
        // Obtener el email del Token (el 'Subject')
        String email = JwtUtil.extractEmail(token);
        
        // Validar el Token y el Email
        // Se valida que el email no sea nulo y que el Token no esté expirado y sea auténtico.
        if (email == null || !JwtUtil.validateToken(token, email)) {
            System.out.println("Validacion Fallida: Token expirado, inválido o falsificado.");
            return null;
        }

        // Buscar el usuario en la base de datos
        Usuario u = repoUsuario.findByEmail(email);

        // Comprobaciones de Estado Final
        if (u == null) {
            System.out.println("Validacion Fallida: Usuario no encontrado para el Token válido.");
            return null;
        }
        
        if (u.isReportado()) {
            System.out.println("Validacion Fallida: Usuario reportado/bloqueado.");
            return null;
        }

        // Éxito: Token válido, usuario activo. Mapear y devolver el DTO.
        System.out.println("Validacion Exitosa: Sesión persistente activa para " + u.getEmail());
        DTOusuarioBajada dtoUser = dtoConverter.map(u, DTOusuarioBajada.class);
        return dtoUser;
    }

	

	/**
     * Registro de usuario con datos mínimos.
     *
     * @param usuarioDto DTO con datos básicos
     * @return DTO del usuario registrado
     */
	@Override
	public DTOusuarioBajada register(DTOusuarioSubidaMinimo usuarioDto) {
		Usuario u = dtoConverter.map(usuarioDto, Usuario.class);

		// Verificar si ya existe un usuario con ese email
		Usuario existente = repoUsuario.findByEmail(u.getEmail());
		if (existente != null) {
			throw new IllegalArgumentException("El email " + u.getEmail() + " ya existe");
		}
		// Codificar contraseña antes de guardar
		u.setPassword(passwordEncoder.encode(u.getPassword()));

		// Cliente por defecto
		if (u.getRol() == null)
			u.setRol(Rol.CLIENTE);
		if (u.getFoto() == null) {
			u.setFoto("https://i.pinimg.com/736x/d9/d8/8e/d9d88e3d1f74e2b8ced3df051cecb81d.jpg"); // foto por defecto
		}
		// Tarjeta por defecto 
		if (u.getTarjeta() == null || u.getTarjeta().getId() == null) {
			
			// Creamos una nueva instancia de TarjetaBancaria con el saldo por defecto
			TarjetaBancaria nuevaTarjeta = new TarjetaBancaria(null, u.getNombre(), null, null, null, BigDecimal.valueOf(500), u);
            // Establecer otros campos de la tarjeta
            nuevaTarjeta.setNombreTitular(u.getNombre()); // Usamos el nombre del usuario
            nuevaTarjeta.setFechaCaducidad(java.time.LocalDate.now()); // O la lógica que uses para la fecha
			//seteamos
            u.setTarjeta(nuevaTarjeta);
		}

		// Carrito por defecto(importante setear usuario)
		if (u.getCarrito() == null) {
			Carrito carrito = new Carrito();
			carrito.setUsuario(u); // Muy importante
			u.setCarrito(carrito);
		}
		System.out.println("Registrando usuario..." + u);
		repoUsuario.save(u);

		return dtoConverter.map(u, DTOusuarioBajada.class);
	}


    /**
     * Registro de usuario con datos completos (mismo que insert()).
     *
     * @param usuarioDto DTO con datos completos
     * @return DTO del usuario registrado
     */
	@Override
	public DTOusuarioBajada registerConDatos(DTOusuarioSubida usuarioDto) {
		return insert(usuarioDto); // misma lógica de insert
	}

	
	 /**
     * Login de usuario.
     * Comprueba email y contraseña (BCrypt) y genera token JWT.
     *
     * @param dtoLogin DTO con credenciales
     * @return DTO con resultado del login y token si es exitoso
     */
	@Override 
	public DTOusuarioLoginBajada login(DTOusuarioLogin dtoLogin) {
		Usuario u = repoUsuario.findByEmail(dtoLogin.getEmail());
		DTOusuarioLoginBajada out = new DTOusuarioLoginBajada();
		

	    if (u == null) {
	        out.setExito(false);
	        out.setMensaje("El correo no está registrado. ¿Deseas crear una cuenta?");
	        return out;
	    }
		
		if (u != null && passwordEncoder.matches(dtoLogin.getPassword(), u.getPassword())) {
			
			//Si el usuario está reportado
	        if (u.isReportado()) { 
	            out.setExito(false);
	            out.setMensaje("Cuenta bloqueada. Por favor, contacte con soporte.");
	            return out; 
	        }
	        
	        DTOusuarioBajada dtoUser = dtoConverter.map(u, DTOusuarioBajada.class);
			String token = JwtUtil.generateToken(u.getEmail());
			
			//En caso de no estar reportado
			out.setDtousuarioBajada(dtoUser);
			out.setToken(token);
			out.setExito(true);
			out.setMensaje("Login correcto");
		} else {
			//En caso que la password sea incorrecta
			out.setExito(false);
			out.setMensaje("Credenciales incorrectas");
		}
		return out;
	}

	 /**
     * Obtiene un usuario reportado por email.
     *
     * @param email Email del usuario
     * @return DTO de usuario reportado o null
     */
	@Override
	public DTOUsuarioReportado findByEmail(String email) {
		// TODO Auto-generated method stub
		Usuario usuario = repoUsuario.findByEmail(email);
		if (usuario != null) {
			return dtoConverter.map(usuario, DTOUsuarioReportado.class);
		}
		return null;
	}

	/**
     * Obtiene todos los eventos creados por un usuario.
     *
     * @param id ID del usuario
     * @return lista de DTOs de eventos
     */
	@Override
	@Transactional
	public List<DTOeventoBajada> findAllEventosCreadosPorUnUsuario(Long id) {
		Optional<Usuario> u = repoUsuario.findById(id);

		if (u.isPresent()) {
			Usuario usu = u.get();
			List<Evento> eventos = usu.getEventosCreados();

			//Mapear la lista de entidades a una lista de DTOs
			return dtoConverter.mapAll(eventos, DTOeventoBajada.class);
		}

		return null; // Devuelve lista vacía de DTOs
	}

	 /**
     * Obtiene todos los usuarios reportados.
     *
     * @return lista de DTOs de usuarios reportados
     */
	@Override
	public List<DTOUsuarioReportado> findAllReportados() {
		// TODO Auto-generated method stub
		List<Usuario> usuariosReportados = repoUsuario.findByReportadoTrue();

		return dtoConverter.mapAll(usuariosReportados, DTOUsuarioReportado.class);
	}

	 /**
     * Cambia el estado de reportado de un usuario.
     *
     * @param email Email del usuario
     * @return DTO del usuario actualizado
     */
	@Override
	public DTOUsuarioReportado reportarUsuario(String email) {
		// TODO Auto-generated method stub
		Usuario usuario = repoUsuario.findByEmail(email);
		if (usuario == null) {
			return null;
		}
		if (!usuario.isReportado()) {
			usuario.setReportado(true);
		} else {
			usuario.setReportado(false);
		}
		repoUsuario.save(usuario);

		DTOUsuarioReportado dto = dtoConverter.map(usuario, DTOUsuarioReportado.class);

		return dto;
	}

	/**
     * Quita el estado de reportado a un usuario.
     *
     * @param email Email del usuario
     * @return DTO del usuario actualizado
     */
	@Override
	public DTOUsuarioReportado quitarReport(String email) {
		// TODO Auto-generated method stub
		Usuario usuario = repoUsuario.findByEmail(email);
		if (usuario == null) {
			return null;
		}

		usuario.setReportado(false);
		repoUsuario.save(usuario);

		DTOUsuarioReportado dto = dtoConverter.map(usuario, DTOUsuarioReportado.class);

		return dto;
	}

}
