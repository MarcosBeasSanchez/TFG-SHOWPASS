package tfg.proyecto.TFG.servicios;

import java.io.IOException;
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
	@Autowired
	ServicioImagenImpl servicioImagen;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
    public DTOusuarioBajada insert(DTOusuarioSubida usuarioDto) {
        // Rol por defecto
        if (usuarioDto.getRol() == null) usuarioDto.setRol(Rol.CLIENTE);

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

        // Hash de contraseña
        if (usuario.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        repoUsuario.save(usuario);
        return dtoConverter.map(usuario, DTOusuarioBajada.class);
    }

	@Override
    public DTOusuarioBajada update(DTOusuarioModificarSubida usuarioDto) {
        Usuario u = repoUsuario.findById(usuarioDto.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Campos editables
        u.setNombre(usuarioDto.getNombre());
        u.setEmail(usuarioDto.getEmail());
        u.setFechaNacimiento(usuarioDto.getFechaNacimiento());
    
        if (usuarioDto.getRol() != null) u.setRol(usuarioDto.getRol());

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
            if (u.getTarjeta() == null) u.setTarjeta(new TarjetaBancaria());
            u.getTarjeta().setNombreTitular(usuarioDto.getCuenta().getNombreTitular());
            u.getTarjeta().setNTarjeta(usuarioDto.getCuenta().getNTarjeta());
            u.getTarjeta().setFechaCaducidad(usuarioDto.getCuenta().getFechaCaducidad());
            u.getTarjeta().setCvv(usuarioDto.getCuenta().getCvv());
            u.getTarjeta().setSaldo(usuarioDto.getCuenta().getSaldo());
        }

        repoUsuario.save(u);
        return dtoConverter.map(u, DTOusuarioBajada.class);
    }

	 @Override
	    public Integer deleteById(Long id) {
	        if (!repoUsuario.existsById(id)) return 0;
	        repoUsuario.deleteById(id);
	        return 1;
	    }

	// devuelve nulo si no lo encuentra
	 @Override
	    public DTOusuarioBajada findById(Long id) {
	        Usuario u = repoUsuario.findById(id)
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	        return dtoConverter.map(u, DTOusuarioBajada.class);
	    }

	@Override
	public List<DTOusuarioBajada> findAllUsuarios() {
		return dtoConverter.mapAll((List<Usuario>) repoUsuario.findAll(), DTOusuarioBajada.class);

	}

	@Override
    public DTOusuarioBajada register(DTOusuarioSubidaMinimo usuarioDto) {
        Usuario u = dtoConverter.map(usuarioDto, Usuario.class);
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        
        if (u.getRol() == null) u.setRol(Rol.CLIENTE);
        if (u.getFoto() == null) {
            u.setFoto("/uploads/defaults/user.png"); // foto por defecto
        }
        // Tarjeta por defecto vacía (opcional)
        if (u.getTarjeta() == null) u.setTarjeta(new TarjetaBancaria());
        repoUsuario.save(u);
        return dtoConverter.map(u, DTOusuarioBajada.class);
    }
	
	 @Override
	    public DTOusuarioBajada registerConDatos(DTOusuarioSubida usuarioDto) {
	        return insert(usuarioDto); // misma lógica de insert
	    }

	@Override //login que compara los hashes de las contraseñas y tmb los emails
	public DTOusuarioLoginBajada login(DTOusuarioLogin dtoLogin) {
        Usuario u = repoUsuario.findByEmail(dtoLogin.getEmail());
        DTOusuarioLoginBajada out = new DTOusuarioLoginBajada();

        if (u != null && passwordEncoder.matches(dtoLogin.getPassword(), u.getPassword())) {
            DTOusuarioBajada dtoUser = dtoConverter.map(u, DTOusuarioBajada.class);
            String token = JwtUtil.generateToken(u.getEmail());

            out.setDtousuarioBajada(dtoUser);
            out.setToken(token);
            out.setExito(true);
            out.setMensaje("Login correcto");
        } else {
            out.setExito(false);
            out.setMensaje("Credenciales incorrectas");
        }
        return out;
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
