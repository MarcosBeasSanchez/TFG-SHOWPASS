package tfg.proyecto.TFG.servicios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import tfg.proyecto.TFG.config.DtoConverter;
import tfg.proyecto.TFG.config.JwtUtil;
import tfg.proyecto.TFG.dtos.DTOUsuarioReportado;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioBajadaMinimo;
import tfg.proyecto.TFG.dtos.DTOusuarioLogin;
import tfg.proyecto.TFG.dtos.DTOusuarioLoginBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioModificarSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioSubidaMinimo;
import tfg.proyecto.TFG.modelo.Rol;
import tfg.proyecto.TFG.modelo.TarjetaBancaria;
import tfg.proyecto.TFG.modelo.Usuario;
import tfg.proyecto.TFG.repositorio.RepositorioCuentaBancaria;
import tfg.proyecto.TFG.repositorio.RepositorioEvento;
import tfg.proyecto.TFG.repositorio.RepositorioTicket;
import tfg.proyecto.TFG.repositorio.RepositorioUsuario;

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

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public DTOusuarioBajada insert(DTOusuarioSubida usuarioDto) {
		Usuario usuario;
		DTOusuarioBajada dtoBajada;
		
		
		if (usuarioDto.getRol() == null || usuarioDto.getRol().toString().isEmpty()) {
		    usuarioDto.setRol(Rol.CLIENTE);
		}


		usuario = dtoConverter.map(usuarioDto, Usuario.class);
		
		
		repoUsuario.save(usuario);

		dtoBajada = dtoConverter.map(usuario, DTOusuarioBajada.class);
		return dtoBajada;
	}

	@Override
	public DTOusuarioBajada update(DTOusuarioModificarSubida usuarioDto) {
	    DTOusuarioBajada dtoBajada;

	    Optional<Usuario> optionalUsuario = repoUsuario.findById(usuarioDto.getId());
	    if (optionalUsuario.isPresent()) {
	        Usuario usuario = optionalUsuario.get();

	        // 🔹 Actualizar solo los campos editables
	        usuario.setNombre(usuarioDto.getNombre());
	        usuario.setEmail(usuarioDto.getEmail());
	        usuario.setFechaNacimiento(usuarioDto.getFechaNacimiento());
	        usuario.setFoto(usuarioDto.getFoto());
	        usuario.setRol(usuarioDto.getRol());
	        usuario.setActivo(usuarioDto.getActivo());

	        //  Contraseña: solo si se envía una nueva
	        if (usuarioDto.getPassword() != null && !usuarioDto.getPassword().isBlank()) {
	            usuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
	        }
	        //  Si password es null o vacío => se mantiene la que ya estaba en la BD

	        //  Tarjeta bancaria (puedes validar nulls aquí también)
	        if (usuarioDto.getCuenta() != null) {
	            if (usuario.getTarjeta() == null) {
	                usuario.setTarjeta(new TarjetaBancaria());
	            }
	            usuario.getTarjeta().setNombreTitular(usuarioDto.getCuenta().getNombreTitular());
	            usuario.getTarjeta().setFechaCaducidad(usuarioDto.getCuenta().getFechaCaducidad());
	            usuario.getTarjeta().setCvv(usuarioDto.getCuenta().getCvv());
	            usuario.getTarjeta().setSaldo(usuarioDto.getCuenta().getSaldo());
	            usuario.getTarjeta().setNTarjeta(usuarioDto.getCuenta().getNTarjeta());
	        }

	        repoUsuario.save(usuario);
	        dtoBajada = dtoConverter.map(usuario, DTOusuarioBajada.class);
	    } else {
	        dtoBajada = null;
	    }

	    return dtoBajada;
	}

	@Override
	public Integer deleteById(Long id) {
		Integer nfilas = 0;

		if (repoUsuario.existsById(id)) {
			repoUsuario.deleteById(id); // elimina el usuario tener cuidado con cuentaBancaria
			nfilas = 1;
		}
		return nfilas;
	}

	// devuelve nulo si no lo encuentra
	@Override
	public DTOusuarioBajada findById(Long id) {
		DTOusuarioBajada dtobajada;
		Usuario usuario;

		if (repoUsuario.existsById(id)) {
			usuario = repoUsuario.findById(id).get();
			dtobajada = dtoConverter.map(usuario, DTOusuarioBajada.class);
		} else {
			dtobajada = null;
		}
		return dtobajada;
	}

	@Override
	public List<DTOusuarioBajada> findAllUsuarios() {
		return dtoConverter.mapAll((List<Usuario>) repoUsuario.findAll(), DTOusuarioBajada.class);

	}

	@Override
	public DTOusuarioBajada register(DTOusuarioSubidaMinimo usuarioDto) {
		Usuario usuario;
		DTOusuarioBajada dtoBajada;

		usuario = dtoConverter.map(usuarioDto, Usuario.class);

		usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // hashear contraseña
		usuario.setFoto("https://i.pinimg.com/736x/d9/d8/8e/d9d88e3d1f74e2b8ced3df051cecb81d.jpg"); //foto por defecto
		usuario.setActivo(true); // activo por defecto
		usuario.setRol(Rol.CLIENTE); // Cliente por defecto
		
		// Valores por defecto de la tarjeta
	    TarjetaBancaria tarjeta = new TarjetaBancaria();
	    tarjeta.setNombreTitular("");
	    tarjeta.setNTarjeta("");
	    tarjeta.setFechaCaducidad(LocalDate.now()); //caduca hoy
	    tarjeta.setCvv("");
	    tarjeta.setSaldo(BigDecimal.ZERO);
		usuario.setTarjeta(tarjeta);
		repoUsuario.save(usuario);

		dtoBajada = dtoConverter.map(usuario, DTOusuarioBajada.class);

		return dtoBajada;
	}
	@Override
	public DTOusuarioBajada registerConDatos(DTOusuarioSubida usuarioDto) {
		Usuario usuario;
		DTOusuarioBajada dtoBajada;

		usuario = dtoConverter.map(usuarioDto, Usuario.class);

		usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // hashear contraseña
		repoUsuario.save(usuario);

		dtoBajada = dtoConverter.map(usuario, DTOusuarioBajada.class);

		return dtoBajada;
	}

	@Override //login que compara los hashes de las contraseñas y tmb los emails
	public DTOusuarioLoginBajada login(DTOusuarioLogin dtologin) {
		Usuario usuario;
		DTOusuarioBajada dtoUsuario;
		DTOusuarioLoginBajada dtoLoginBajada = new DTOusuarioLoginBajada();

		usuario = repoUsuario.findByEmail(dtologin.getEmail());

		if (usuario != null && passwordEncoder.matches(dtologin.getPassword(), usuario.getPassword())) {

			dtoUsuario = dtoConverter.map(usuario, DTOusuarioBajada.class);
			
			// Generar token JWT
	        String token = JwtUtil.generateToken(usuario.getEmail());
			
			dtoLoginBajada.setDtousuarioBajada(dtoUsuario);
			dtoLoginBajada.setToken(token);
			dtoLoginBajada.setExito(true);
			dtoLoginBajada.setMensaje("Login " + dtoUsuario.getEmail() +" realizado correctamente" );
			//FALTA POR HACER EL TOKEN
			
		}else {
			dtoLoginBajada.setDtousuarioBajada(null);
			dtoLoginBajada.setExito(false);
			dtoLoginBajada.setMensaje("Error Login " );
			
		}
		return dtoLoginBajada;

		
	}

	@Override
	public DTOUsuarioReportado findByEmail(String email) {
		// TODO Auto-generated method stub
		Usuario usuario = repoUsuario.findByEmail(email);
	    if (usuario != null) {
	        return dtoConverter.map(usuario, DTOUsuarioReportado.class);
	    }
	    return null;
	}

	@Override
	public List<DTOUsuarioReportado> findAllReportados() {
		// TODO Auto-generated method stub
		List<Usuario> usuariosReportados = repoUsuario.findByReportadoTrue();
		
		return dtoConverter.mapAll(usuariosReportados, DTOUsuarioReportado.class);
	}

	@Override
	public DTOUsuarioReportado reportarUsuario(String email) {
		// TODO Auto-generated method stub
			Usuario 
			usuario = repoUsuario.findByEmail(email);
	        if (usuario == null) {
	            return null;
	        }

	        usuario.setReportado(true);
	        repoUsuario.save(usuario);

	        DTOUsuarioReportado dto = dtoConverter.map(usuario, DTOUsuarioReportado.class);
	        
	        return dto;
	}

	@Override
	public DTOUsuarioReportado quitarReport(String email) {
		// TODO Auto-generated method stub
		Usuario 
		usuario = repoUsuario.findByEmail(email);
        if (usuario == null) {
            return null;
        }

        usuario.setReportado(false);
        repoUsuario.save(usuario);

        DTOUsuarioReportado dto = dtoConverter.map(usuario, DTOUsuarioReportado.class);
        
        return dto;
	}

}
