package tfg.proyecto.TFG.controladores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tfg.proyecto.TFG.dtos.DTOInvitado;
import tfg.proyecto.TFG.dtos.DTOcuentaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;
import tfg.proyecto.TFG.modelo.Rol;
import tfg.proyecto.TFG.servicios.IServicioCuentaBancaria;
import tfg.proyecto.TFG.servicios.IServicioEvento;
import tfg.proyecto.TFG.servicios.IServicioTicket;
import tfg.proyecto.TFG.servicios.IServicioUsuario;

@RestController
@RequestMapping("/tfg/utilidades")

public class CargarDatos {
	
	public static boolean datosCargados = false;

	
	@Autowired
	IServicioCuentaBancaria daoCuentaBancaria;
	@Autowired
	IServicioEvento daoEvento;
	@Autowired 
	IServicioTicket daoTicket;
	@Autowired
	IServicioUsuario daoUsuario;
	
	@GetMapping("/cargarDatos")
	public ResponseEntity<String>cargarDatos(){
		ResponseEntity<String> res = null;
		
		
		if (!datosCargados) {
			
			
			DTOusuarioSubida u1;
			DTOcuentaBancariaSubida c1;
			DTOeventoSubida e1;
			DTOticketSubida t1;
			
			c1 = DTOcuentaBancariaSubida.builder()
					.nombrePropietario("n")
					.nombreBanco("Caixa")
					.IBAN("ES6112343456420456323532")
					.BIC("BBVAESMMooo")
					.saldo(BigDecimal.valueOf(200.50))
					.build();
			
			
			u1 = DTOusuarioSubida.builder()
					.nombre("nombrePrueba")
					.email("email@mail.com")
					.password("1234")
					.fechaNacimiento(LocalDate.now().minusYears(25))
					.rol(Rol.ADMIN)
					.cuenta(c1)
					.activo(true)
					.build();
			
			DTOusuarioBajada usuarioBajada = daoUsuario.insert(u1);
			
			e1 = DTOeventoSubida.builder()
				    .nombre("Fiesta")
				    .localizacion("Madrid")
				    .inicioEvento(LocalDateTime.of(2025, 5, 4, 22, 0))
				    .finEvento(LocalDateTime.now().plusHours(5))
				    .invitados(List.of(
				        DTOInvitado.builder()
				            .nombre("Juan")
				            .apellidos("Pérez")
				            .fotoURL("https://ejemplo.com/foto.jpg")
				            .descripcion("Amigo de la infancia")
				            .build()
				    ))
				    .build();
			
			DTOeventoBajada eventoBajada =  daoEvento.insert(e1);
			
			
			

			t1 = DTOticketSubida.builder()
					.usuarioId(usuarioBajada.getId())
					.eventoId(eventoBajada.getId())
					.build();
			
			daoTicket.insert(t1);		
			
			res = new ResponseEntity<String>(" Cargando Datos",HttpStatus.CREATED);
		} else {
			res = new ResponseEntity<String>("Error Cargando Datos",HttpStatus.NOT_FOUND);
		}
		
	    datosCargados = true;
		
		return res;
		
	}
}
