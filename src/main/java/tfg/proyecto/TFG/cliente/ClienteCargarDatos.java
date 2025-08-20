package tfg.proyecto.TFG.cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

@Component
public class ClienteCargarDatos implements CommandLineRunner {

    private static boolean datosCargados = false;

    @Autowired
    IServicioCuentaBancaria daoCuentaBancaria;

    @Autowired
    IServicioEvento daoEvento;

    @Autowired
    IServicioTicket daoTicket;

    @Autowired
    IServicioUsuario daoUsuario;

    @Override
    public void run(String... args) throws Exception {
        if (!datosCargados) {
        	
			DTOusuarioSubida u1;
			DTOcuentaBancariaSubida c1;

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
			
			DTOeventoSubida e1 = DTOeventoSubida.builder()
				    .nombre("Festival de Música Electrónica")
				    .localizacion("Madrid, España")
				    .inicioEvento(LocalDateTime.of(2025, 7, 15, 18, 0))
				    .finEvento(LocalDateTime.of(2025, 7, 15, 23, 59))
				    .imagen("https://plus.unsplash.com/premium_photo-1723914048561-12a00dd83ec6?q=80&w=1492&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
				    .invitados(List.of(
				        DTOInvitado.builder().nombre("Carlos").apellidos("Gómez").fotoURL("https://randomuser.me/api/portraits/men/1.jpg").descripcion("DJ residente en Madrid").build(),
				        DTOInvitado.builder().nombre("Laura").apellidos("Martínez").fotoURL("https://randomuser.me/api/portraits/women/2.jpg").descripcion("Productora musical y cantante").build()
				    ))
				    .build();

				// Evento 2
				DTOeventoSubida e2 = DTOeventoSubida.builder()
				    .nombre("Concierto de Rock")
				    .localizacion("Barcelona, España")
				    .inicioEvento(LocalDateTime.of(2025, 8, 10, 20, 0))
				    .finEvento(LocalDateTime.of(2025, 8, 10, 23, 0))
				    .imagen("https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
				    .invitados(List.of(
				        DTOInvitado.builder().nombre("Miguel").apellidos("López").fotoURL("https://randomuser.me/api/portraits/men/3.jpg").descripcion("Guitarrista principal").build(),
				        DTOInvitado.builder().nombre("Sofía").apellidos("Ramírez").fotoURL("https://randomuser.me/api/portraits/women/4.jpg").descripcion("Baterista").build()
				    ))
				    .build();

				// Evento 3
				DTOeventoSubida e3 = DTOeventoSubida.builder()
				    .nombre("Exposición de Arte Moderno")
				    .localizacion("Valencia, España")
				    .inicioEvento(LocalDateTime.of(2025, 9, 5, 10, 0))
				    .finEvento(LocalDateTime.of(2025, 9, 5, 18, 0))
				    .imagen("https://plus.unsplash.com/premium_photo-1706548911781-dd3ad17a8fa6?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
				    .invitados(List.of(
				        DTOInvitado.builder().nombre("Ana").apellidos("García").fotoURL("https://randomuser.me/api/portraits/women/5.jpg").descripcion("Curadora de la exposición").build(),
				        DTOInvitado.builder().nombre("Javier").apellidos("Hernández").fotoURL("https://randomuser.me/api/portraits/men/6.jpg").descripcion("Artista invitado").build()
				    ))
				    .build();

				// Evento 4
				DTOeventoSubida e4 = DTOeventoSubida.builder()
				    .nombre("Torneo de eSports")
				    .localizacion("Sevilla, España")
				    .inicioEvento(LocalDateTime.of(2025, 10, 20, 14, 0))
				    .finEvento(LocalDateTime.of(2025, 10, 20, 22, 0))
				    .imagen("https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
				    .invitados(List.of(
				        DTOInvitado.builder().nombre("Lucas").apellidos("Moreno").fotoURL("https://randomuser.me/api/portraits/men/7.jpg").descripcion("Jugador profesional de League of Legends").build(),
				        DTOInvitado.builder().nombre("Marta").apellidos("Sánchez").fotoURL("https://randomuser.me/api/portraits/women/8.jpg").descripcion("Streamer invitada").build()
				    ))
				    .build();

				// Evento 5
				DTOeventoSubida e5 = DTOeventoSubida.builder()
				    .nombre("Maratón Solidario")
				    .localizacion("Bilbao, España")
				    .inicioEvento(LocalDateTime.of(2025, 11, 2, 9, 0))
				    .finEvento(LocalDateTime.of(2025, 11, 2, 15, 0))
				    .imagen("https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?q=80&w=1474&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
				    .invitados(List.of(
				        DTOInvitado.builder().nombre("Elena").apellidos("Torres").fotoURL("https://randomuser.me/api/portraits/women/9.jpg").descripcion("Organizadora del evento").build(),
				        DTOInvitado.builder().nombre("Pedro").apellidos("Alonso").fotoURL("https://randomuser.me/api/portraits/men/10.jpg").descripcion("Atleta invitado").build()
				    ))
				    .build();
			
			DTOeventoBajada eventoBajada =  daoEvento.insert(e1);
			
			daoEvento.insert(e2);
			daoEvento.insert(e3);
			daoEvento.insert(e4);
			daoEvento.insert(e5);
			
			
			

			t1 = DTOticketSubida.builder()
					.usuarioId(usuarioBajada.getId())
					.eventoId(eventoBajada.getId())
					.build();
			
			daoTicket.insert(t1);
			
			
            datosCargados = true;
            System.out.println("Datos de ejemplo cargados correctamente.");
        }
    }
}
