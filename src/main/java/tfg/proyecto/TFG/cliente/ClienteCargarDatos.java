package tfg.proyecto.TFG.cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import tfg.proyecto.TFG.dtos.DTOInvitado;
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

@Component
public class ClienteCargarDatos implements CommandLineRunner {

	private static boolean datosCargados = false;

	@Autowired
	IServicioTarjetaBancaria daoCuentaBancaria;

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
			DTOtarjetaBancariaSubida c1;

			DTOticketSubida t1;

			c1 = DTOtarjetaBancariaSubida.builder().nombreTitular("nombreTitular").nTarjeta("1234567891011121")
					.fechaCaducidad(LocalDate.of(2027, 10, 10)).cvv("123").saldo(BigDecimal.valueOf(2000.50)).build();

			u1 = DTOusuarioSubida.builder().nombre("nombrePrueba").email("email@mail.com").password("1234")
					.fechaNacimiento(LocalDate.now().minusYears(25)).rol(Rol.ADMIN).cuenta(c1).activo(true).build();

			DTOusuarioBajada usuarioBajada = daoUsuario.insert(u1);

			DTOeventoSubida e1 = DTOeventoSubida.builder().nombre("Festival de Música Electrónica")
					.localizacion("Madrid, España").inicioEvento(LocalDateTime.of(2025, 7, 15, 18, 0))
					.finEvento(LocalDateTime.of(2025, 7, 15, 23, 59))
			        .descripcion("Un festival vibrante en Madrid con los mejores DJs y productores de música electrónica.")
			        .carrusel("https://tse2.mm.bing.net/th/id/OIP.l-8N1EsbfpIemn0l3gQFAgHaEK?pid=Api&P=0&h=180")
			        .carrusel("https://tse2.mm.bing.net/th/id/OIP.LrlVbJ18dd6unly6VPa9BgHaEE?pid=Api&P=0&h=180")
			        .carrusel("https://tse3.mm.bing.net/th/id/OIP.GctXbUYXtIRd7FupgJXZ7AHaE8?pid=Api&P=0&h=180")
			        .carrusel("https://tse3.mm.bing.net/th/id/OIP.cZSoJSG1Vz-BSyYo3_e7IAHaEE?pid=Api&P=0&h=180")
			        .carrusel("https://tse3.mm.bing.net/th/id/OIP.AHEqm3auKGjPDOoc2VzPBQHaFj?pid=Api&P=0&h=180")
					.imagen("https://plus.unsplash.com/premium_photo-1723914048561-12a00dd83ec6?q=80&w=1492&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(1)
					.invitados(List.of(DTOInvitado.builder().nombre("Carlos").apellidos("Gómez")
							.fotoURL("https://tse3.mm.bing.net/th/id/OIP.J7mTXL5Pjfk7_ik14ZfQGgHaE8?pid=Api&P=0&h=180")
							.descripcion("DJ residente en Madrid").build(),
							DTOInvitado.builder().nombre("Laura").apellidos("Martínez").fotoURL(
									"https://tse3.mm.bing.net/th/id/OIP.w4VVB987sXxX1KNBLP-qSwHaEL?pid=Api&P=0&h=180")
									.descripcion("Productora musical y cantante").build()))
					.build();

			// Evento 2
			DTOeventoSubida e2 = DTOeventoSubida.builder().nombre("Concierto de Rock").localizacion("Barcelona, España")
					.inicioEvento(LocalDateTime.of(2025, 8, 10, 20, 0)).finEvento(LocalDateTime.of(2025, 8, 10, 23, 0))
					.imagen("https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .descripcion("Una noche inolvidable con las mejores bandas de rock en Barcelona.")
			        .carrusel("https://tse1.mm.bing.net/th/id/OIP.5HUg63uzydu058UmpeyqtAHaEJ?pid=Api&P=0&h=180")
			        .carrusel("https://tse3.mm.bing.net/th/id/OIP.sOACtIquBRsn4VvrDTkd5QHaEo?pid=Api&P=0&h=180")
			        .precio(1)
			        .invitados(List.of(DTOInvitado.builder().nombre("Miguel").apellidos("López").fotoURL(
							"https://media.gamestop.com/i/gamestop/11121573_ALT02/PureArts-Cyberpunk2077-Johnny-Silverhand-with-the-RockerBoys-Guitar-Statue?fmt=auto")
							.descripcion("Guitarrista principal").build(),
							DTOInvitado.builder().nombre("Sofía").apellidos("Ramírez").fotoURL(
									"https://tse4.mm.bing.net/th/id/OIP.J6vLzNZP4tL_emdZzg8DDQHaE8?pid=Api&P=0&h=180")
									.descripcion("Baterista").build()))
					.build();

			// Evento 3
			DTOeventoSubida e3 = DTOeventoSubida.builder().nombre("Exposición de Arte Moderno")
					.localizacion("Valencia, España").inicioEvento(LocalDateTime.of(2025, 9, 5, 10, 0))
					.finEvento(LocalDateTime.of(2025, 9, 5, 18, 0))
			        .descripcion("Una exposición única en Valencia que reúne a destacados artistas contemporáneos.")
					.carrusel("https://tse3.mm.bing.net/th/id/OIP.9HbkSV5kGVDGtakCq8sVzgHaE7?pid=Api&P=0&h=180")
					.carrusel("https://tse1.mm.bing.net/th/id/OIP.fFzO0QsJMBbuwquWA-4YvQHaEK?pid=Api&P=0&h=180")
			        .imagen("https://plus.unsplash.com/premium_photo-1706548911781-dd3ad17a8fa6?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .precio(1)
			        .invitados(List.of(DTOInvitado.builder().nombre("Ana").apellidos("García")
							.fotoURL("https://tse2.mm.bing.net/th/id/OIP.eWcoK30BjXpz7CO3rkmaqgHaHa?pid=Api&P=0&h=180")
							.descripcion("Curadora de la exposición").build(),
							DTOInvitado.builder().nombre("Javier").apellidos("Hernández").fotoURL(
									"https://tse2.mm.bing.net/th/id/OIP.kavocgBTSr8p3YmM2GTooAHaEK?pid=Api&P=0&h=180g")
									.descripcion("Artista invitado").build()))
					.build();

			// Evento 4
			DTOeventoSubida e4 = DTOeventoSubida.builder().nombre("Torneo de eSports").localizacion("Sevilla, España")
					.inicioEvento(LocalDateTime.of(2025, 10, 20, 14, 0))
					.finEvento(LocalDateTime.of(2025, 10, 20, 22, 0))
			        .descripcion("El torneo más esperado de videojuegos en Sevilla con jugadores profesionales y streamers.")
			        .carrusel("https://tse4.mm.bing.net/th/id/OIP.HR2i7jzEjbp1xFTRSiYimQHaDj?pid=Api&P=0&h=180")
			        .carrusel("https://tse2.mm.bing.net/th/id/OIP.yoMK91Uyjj98-XBw23KvHgHaEK?pid=Api&P=0&h=180")
					.imagen("https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(1)
					.invitados(List.of(DTOInvitado.builder().nombre("Lucas").apellidos("Moreno")
							.fotoURL("https://tse3.mm.bing.net/th/id/OIP.sSPR3mdA7rRLoQ9Y2MURkgAAAA?pid=Api&P=0&h=180")
							.descripcion("Jugador profesional de League of Legends").build(),
							DTOInvitado.builder().nombre("Marta").apellidos("Sánchez")
									.fotoURL("https://randomuser.me/api/portraits/women/50.jpg")
									.descripcion("Streamer invitada").build()))
					.build();

			// Evento 5
			DTOeventoSubida e5 = DTOeventoSubida.builder().nombre("Maratón Solidario").localizacion("Bilbao, España")
					.inicioEvento(LocalDateTime.of(2025, 11, 2, 9, 0)).finEvento(LocalDateTime.of(2025, 11, 2, 15, 0))
			        .descripcion("Un evento deportivo en Bilbao para recaudar fondos y promover la solidaridad.")
			        .carrusel("https://tse1.mm.bing.net/th/id/OIP.bP5JHW1amdag2ADTO5c2NgHaHa?pid=Api&P=0&h=180")
			        .carrusel("https://tse2.mm.bing.net/th/id/OIP.ANZHqasJ7f7VtTMIDUSoHgHaEK?pid=Api&P=0&h=180")
					.imagen("https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?q=80&w=1474&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(1)
					.invitados(List.of(DTOInvitado.builder().nombre("Elena").apellidos("Torres").fotoURL(
							"https://i0.wp.com/www.soycorredora.com/wp-content/uploads/2017/08/Screen-Shot-2017-08-09-at-10.58.34-PM.png?fit=631%2C475&ssl=1")
							.descripcion("Organizadora del evento").build(),
							DTOInvitado.builder().nombre("Pedro").apellidos("Alonso").fotoURL(
									"https://tse3.mm.bing.net/th/id/OIP.eEsLZ2jj2AQ7HFGYvyEcGgHaEK?pid=Api&P=0&h=180")
									.descripcion("Atleta invitado").build()))
					.build();

			DTOeventoSubida e6 = DTOeventoSubida.builder().nombre("Carrera de Aventura Montaña y Río")
					.localizacion("Pirineos, España").inicioEvento(LocalDateTime.of(2025, 9, 12, 8, 0))
					.finEvento(LocalDateTime.of(2025, 9, 12, 17, 0))
			        .descripcion("Una prueba extrema en los Pirineos que combina montaña, río y resistencia física.")
			        .carrusel("https://tse3.mm.bing.net/th/id/OIP.msl73M6jPj7z74ItgusrmgHaE7?pid=Api&P=0&h=180")
					.imagen("https://plus.unsplash.com/premium_photo-1664301432574-9b4e85c2b2d3?q=80&w=1471&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(1)
					.invitados(List.of(
							DTOInvitado.builder().nombre("Sofía").apellidos("Jiménez")
									.fotoURL("https://randomuser.me/api/portraits/women/65.jpg")
									.descripcion("Guía de montaña y organizadora").build(),
							DTOInvitado.builder().nombre("Miguel").apellidos("Sánchez")
									.fotoURL("https://randomuser.me/api/portraits/men/41.jpg")
									.descripcion("Atleta de aventura invitado").build()))
					.build();

			DTOeventoSubida e7 = DTOeventoSubida.builder().nombre("Feria Internacional del Libro")
					.localizacion("Barcelona, España").inicioEvento(LocalDateTime.of(2025, 4, 20, 10, 0))
					.finEvento(LocalDateTime.of(2025, 4, 25, 20, 0))
			        .descripcion("Una feria literaria en Barcelona que reúne a escritores, editores y amantes de los libros.")
			        .carrusel("https://tse2.mm.bing.net/th/id/OIP.Os-7DnB2sRjtMcbt6F9vUAHaEK?pid=Api&P=0&h=180")
					.imagen("https://images.unsplash.com/photo-1519682337058-a94d519337bc")
					.precio(1)
					.invitados(List.of(
							DTOInvitado.builder().nombre("Isabel").apellidos("Allende")
									.fotoURL("https://randomuser.me/api/portraits/women/50.jpg")
									.descripcion("Escritora invitada").build(),
							DTOInvitado.builder().nombre("Juan").apellidos("Gómez Jurado")
									.fotoURL("https://randomuser.me/api/portraits/men/61.jpg")
									.descripcion("Autor de thrillers españoles").build()))
					.build();

			DTOeventoSubida e8 = DTOeventoSubida.builder().nombre("Torneo Juvenil de Fútbol")
					.localizacion("Sevilla, España").inicioEvento(LocalDateTime.of(2025, 6, 15, 9, 0))
					.finEvento(LocalDateTime.of(2025, 6, 15, 18, 0))
			        .descripcion("Una jornada deportiva en Sevilla con jóvenes talentos del fútbol.")
			        .carrusel("https://tse1.mm.bing.net/th/id/OIP.qdFIbxtvXNB9EtdltQUXyAHaE8?pid=Api&P=0&h=180")
			        .carrusel("https://tse2.mm.bing.net/th/id/OIP.HWb_-MldumPPd8UZfx3o_AHaE8?pid=Api&P=0&h=180")
					.imagen("https://images.unsplash.com/photo-1598880513655-d1c6d4b2dfbf?q=80&w=1548&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(1)
					.invitados(List.of(
							DTOInvitado.builder().nombre("Andrés").apellidos("Iniesta")
									.fotoURL("https://randomuser.me/api/portraits/men/70.jpg")
									.descripcion("Exjugador profesional, invitado especial").build(),
							DTOInvitado.builder().nombre("Marta").apellidos("López")
									.fotoURL("https://randomuser.me/api/portraits/women/22.jpg")
									.descripcion("Entrenadora de fútbol femenino").build()))
					.build();

			DTOeventoSubida e9 = DTOeventoSubida.builder().nombre("Tech Future Conference")
					.localizacion("Valencia, España").inicioEvento(LocalDateTime.of(2025, 10, 5, 9, 0))
					.finEvento(LocalDateTime.of(2025, 10, 7, 18, 0))
			        .descripcion("Una conferencia tecnológica en Valencia sobre innovación, IA y el futuro digital.")
			        .carrusel("https://tse3.mm.bing.net/th/id/OIP.SkMEhv3ji9cjcwWDiSsrmwHaE8?pid=Api&P=0&h=180")
			        .carrusel("https://tse1.mm.bing.net/th/id/OIP.9iRcAvasWSHOxx7xv5H-hAHaEK?pid=Api&P=0&h=180")
			        .precio(1)
			        .imagen("https://images.unsplash.com/photo-1519389950473-47ba0277781c")
					.invitados(List.of(
							DTOInvitado.builder().nombre("Ana").apellidos("García")
									.fotoURL("https://randomuser.me/api/portraits/women/30.jpg")
									.descripcion("CEO de startup tecnológica").build(),
							DTOInvitado.builder().nombre("David").apellidos("Fernández")
									.fotoURL("https://randomuser.me/api/portraits/men/80.jpg")
									.descripcion("Experto en inteligencia artificial").build()))
					.build();

			DTOeventoSubida e10 = DTOeventoSubida.builder().nombre("Exposición de Arte Contemporáneo")
					.localizacion("Valencia, España").inicioEvento(LocalDateTime.of(2025, 7, 3, 11, 0))
					.finEvento(LocalDateTime.of(2025, 7, 3, 20, 0))
			        .descripcion("Un espacio en Valencia para explorar lo más reciente del arte contemporáneo.")
			        .carrusel("https://tse2.mm.bing.net/th/id/OIP.nhQZn_l-I8AwwWmNj5D32gHaEq?pid=Api&P=0&h=180")
			        .precio(1)
			        .imagen("https://images.unsplash.com/photo-1547891654-e66ed7ebb968?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.invitados(List.of(
							DTOInvitado.builder().nombre("Lucía").apellidos("Morales")
									.fotoURL("https://randomuser.me/api/portraits/women/60.jpg")
									.descripcion("Artista expositora").build(),
							DTOInvitado.builder().nombre("Pablo").apellidos("Ruiz")
									.fotoURL("https://randomuser.me/api/portraits/men/50.jpg")
									.descripcion("Crítico de arte invitado").build()))
					.build();

			DTOeventoBajada eventoBajada = daoEvento.insert(e1);

			daoEvento.insert(e2);
			daoEvento.insert(e3);
			daoEvento.insert(e4);
			daoEvento.insert(e5);
			daoEvento.insert(e6);
			daoEvento.insert(e7);
			daoEvento.insert(e8);
			daoEvento.insert(e9);
			daoEvento.insert(e10);

			t1 = DTOticketSubida.builder().usuarioId(usuarioBajada.getId()).eventoId(eventoBajada.getId()).build();

			daoTicket.insert(t1);

			datosCargados = true;
			System.out.println("Datos de ejemplo cargados correctamente.");
		}
	}
}
