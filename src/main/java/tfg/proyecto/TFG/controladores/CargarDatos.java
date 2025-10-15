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

import tfg.proyecto.TFG.dtos.DTOtarjetaBancariaSubida;
import tfg.proyecto.TFG.dtos.DTOeventoBajada;
import tfg.proyecto.TFG.dtos.DTOeventoSubida;
import tfg.proyecto.TFG.dtos.DTOticketSubida;
import tfg.proyecto.TFG.dtos.DTOusuarioBajada;
import tfg.proyecto.TFG.dtos.DTOusuarioSubida;
import tfg.proyecto.TFG.modelo.Rol;
import tfg.proyecto.TFG.servicios.IServicioTarjetaBancaria;
import tfg.proyecto.TFG.servicios.IServicioEvento;
import tfg.proyecto.TFG.servicios.IServicioTicket;
import tfg.proyecto.TFG.servicios.IServicioUsuario;

@RestController
@RequestMapping("/tfg/utilidades")

public class CargarDatos {
//	
//	public static boolean datosCargados = false;
//
//	
//	@Autowired
//	IServicioTarjetaBancaria daoCuentaBancaria;
//	@Autowired
//	IServicioEvento daoEvento;
//	@Autowired 
//	IServicioTicket daoTicket;
//	@Autowired
//	IServicioUsuario daoUsuario;
//	
//	@GetMapping("/cargarDatos")
//	public ResponseEntity<String>cargarDatos(){
//		ResponseEntity<String> res = null;
//		
//		
//		if (!datosCargados) {
//			
//			
//			DTOusuarioSubida u1;
//			DTOtarjetaBancariaSubida c1;
//
//			DTOticketSubida t1;
//			
//			c1 = DTOtarjetaBancariaSubida.builder()
//					.nombreTitular("nombreTitular")
//					.nTarjeta("1234567891011121")
//					.fechaCaducidad(LocalDate.of(2027, 10, 10))
//					.cvv("123")
//					.saldo(BigDecimal.valueOf(2000.50))
//					.build();
//			
//			
//			u1 = DTOusuarioSubida.builder()
//					.nombre("nombrePrueba")
//					.email("email@mail.com")
//					.password("1234")
//					.fechaNacimiento(LocalDate.now().minusYears(25))
//					.foto("https://i.pinimg.com/736x/d9/d8/8e/d9d88e3d1f74e2b8ced3df051cecb81d.jpg")
//					.rol(Rol.ADMIN)
//					.cuenta(c1)
//					.activo(true)
//					.build();
//			
//			DTOusuarioBajada usuarioBajada = daoUsuario.insert(u1);
//			
//			DTOeventoSubida e1 = DTOeventoSubida.builder()
//				    .nombre("Festival de Música Electrónica")
//				    .localizacion("Madrid, España")
//				    .inicioEvento(LocalDateTime.of(2025, 7, 15, 18, 0))
//				    .finEvento(LocalDateTime.of(2025, 7, 15, 23, 59))
//				    .imagen("https://images.unsplash.com/photo-1506748686217-1a2a9b1b8e2b?auto=format&fit=crop&w=800&q=80")
//				    .invitados(List.of(
//				        DTOInvitado.builder().nombre("Carlos").apellidos("Gómez").fotoURL("https://randomuser.me/api/portraits/men/1.jpg").descripcion("DJ residente en Madrid").build(),
//				        DTOInvitado.builder().nombre("Laura").apellidos("Martínez").fotoURL("https://randomuser.me/api/portraits/women/2.jpg").descripcion("Productora musical y cantante").build()
//				    ))
//				    .build();
//
//				// Evento 2
//				DTOeventoSubida e2 = DTOeventoSubida.builder()
//				    .nombre("Concierto de Rock")
//				    .localizacion("Barcelona, España")
//				    .inicioEvento(LocalDateTime.of(2025, 8, 10, 20, 0))
//				    .finEvento(LocalDateTime.of(2025, 8, 10, 23, 0))
//				    .imagen("https://images.unsplash.com/photo-1518972559570-3e2b8a6ed4e4?auto=format&fit=crop&w=800&q=80")
//				    .invitados(List.of(
//				        DTOInvitado.builder().nombre("Miguel").apellidos("López").fotoURL("https://randomuser.me/api/portraits/men/3.jpg").descripcion("Guitarrista principal").build(),
//				        DTOInvitado.builder().nombre("Sofía").apellidos("Ramírez").fotoURL("https://randomuser.me/api/portraits/women/4.jpg").descripcion("Baterista").build()
//				    ))
//				    .build();
//
//				// Evento 3
//				DTOeventoSubida e3 = DTOeventoSubida.builder()
//				    .nombre("Exposición de Arte Moderno")
//				    .localizacion("Valencia, España")
//				    .inicioEvento(LocalDateTime.of(2025, 9, 5, 10, 0))
//				    .finEvento(LocalDateTime.of(2025, 9, 5, 18, 0))
//				    .imagen("https://images.unsplash.com/photo-1549887534-9a1fdd5c5b0b?auto=format&fit=crop&w=800&q=80")
//				    .invitados(List.of(
//				        DTOInvitado.builder().nombre("Ana").apellidos("García").fotoURL("https://randomuser.me/api/portraits/women/5.jpg").descripcion("Curadora de la exposición").build(),
//				        DTOInvitado.builder().nombre("Javier").apellidos("Hernández").fotoURL("https://randomuser.me/api/portraits/men/6.jpg").descripcion("Artista invitado").build()
//				    ))
//				    .build();
//
//				// Evento 4
//				DTOeventoSubida e4 = DTOeventoSubida.builder()
//				    .nombre("Torneo de eSports")
//				    .localizacion("Sevilla, España")
//				    .inicioEvento(LocalDateTime.of(2025, 10, 20, 14, 0))
//				    .finEvento(LocalDateTime.of(2025, 10, 20, 22, 0))
//				    .imagen("https://images.unsplash.com/photo-1615047497486-1bbf6b1b61b0?auto=format&fit=crop&w=800&q=80")
//				    .invitados(List.of(
//				        DTOInvitado.builder().nombre("Lucas").apellidos("Moreno").fotoURL("https://randomuser.me/api/portraits/men/7.jpg").descripcion("Jugador profesional de League of Legends").build(),
//				        DTOInvitado.builder().nombre("Marta").apellidos("Sánchez").fotoURL("https://randomuser.me/api/portraits/women/8.jpg").descripcion("Streamer invitada").build()
//				    ))
//				    .build();
//
//				// Evento 5
//				DTOeventoSubida e5 = DTOeventoSubida.builder()
//				    .nombre("Maratón Solidario")
//				    .localizacion("Bilbao, España")
//				    .inicioEvento(LocalDateTime.of(2025, 11, 2, 9, 0))
//				    .finEvento(LocalDateTime.of(2025, 11, 2, 15, 0))
//				    .imagen("https://images.unsplash.com/photo-1571019613910-4f3a5fcd4640?auto=format&fit=crop&w=800&q=80")
//				    .invitados(List.of(
//				        DTOInvitado.builder().nombre("Elena").apellidos("Torres").fotoURL("https://randomuser.me/api/portraits/women/9.jpg").descripcion("Organizadora del evento").build(),
//				        DTOInvitado.builder().nombre("Pedro").apellidos("Alonso").fotoURL("https://randomuser.me/api/portraits/men/10.jpg").descripcion("Atleta invitado").build()
//				    ))
//				    .build();
//			
//			DTOeventoBajada eventoBajada =  daoEvento.insert(e1);
//			
//			daoEvento.insert(e2);
//			daoEvento.insert(e3);
//			daoEvento.insert(e4);
//			daoEvento.insert(e5);
//			
//
//			t1 = DTOticketSubida.builder()
//					.usuarioId(usuarioBajada.getId())
//					.eventoId(eventoBajada.getId())
//					.build();
//			
//			daoTicket.insert(t1);		
//			
//			res = new ResponseEntity<String>(" Cargando Datos",HttpStatus.CREATED);
//		} else {
//			res = new ResponseEntity<String>("Error Cargando Datos",HttpStatus.NOT_FOUND);
//		}
//		
//	    datosCargados = true;
//		
//		return res;
//		
//	}
}
