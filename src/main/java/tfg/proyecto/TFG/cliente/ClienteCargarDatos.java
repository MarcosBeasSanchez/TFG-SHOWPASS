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

			c1 = DTOtarjetaBancariaSubida.builder()
					.nombreTitular("nombreTitular")
					.nTarjeta("12345678910")
					.fechaCaducidad(LocalDate.of(2027, 10, 10))
					.cvv("123")
					.saldo(BigDecimal.valueOf(2000.50))
					.build();

			u1 = DTOusuarioSubida.builder()
					.nombre("usuarioPrueba")
					.email("prueba@mail.com")
					.password("1234")
					.foto("https://i.pinimg.com/736x/d9/d8/8e/d9d88e3d1f74e2b8ced3df051cecb81d.jpg")
					.fechaNacimiento(LocalDate.now().minusYears(25))
					.rol(Rol.ADMIN)
					.cuenta(c1)
					.activo(true)
					.cuenta(c1)
					.build();
			
			DTOusuarioBajada usuarioBajada = daoUsuario.registerConDatos(u1);
			//DTOusuarioBajada usuarioBajada = daoUsuario.insert(u1);

			DTOeventoSubida e1 = DTOeventoSubida.builder().nombre("Festival de Música Electrónica")
					.localizacion("Madrid, España").inicioEvento(LocalDateTime.of(2025, 7, 15, 18, 0))
					.finEvento(LocalDateTime.of(2025, 7, 15, 23, 59))
					.descripcion("Un festival vibrante en Madrid que reúne a los mejores DJs y productores de música electrónica de todo el mundo. Este evento promete una experiencia inolvidable con espectáculos de luces impresionantes, escenarios de última generación y un ambiente lleno de energía. Los asistentes podrán disfrutar de sets exclusivos, colaboraciones únicas y una atmósfera que celebra la pasión por la música electrónica. Además, habrá zonas de descanso, áreas de comida gourmet y actividades interactivas para garantizar una experiencia completa. Es el lugar perfecto para los amantes de la música y la fiesta, donde la creatividad y la tecnología se unen para ofrecer una noche mágica en el corazón de Madrid.")
			        .carrusel("https://plus.unsplash.com/premium_photo-1661284892176-fd7713b764a6?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1661377118520-287ec60a32f3?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1663051210654-0c8a835dad1f?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=1548&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.imagen("https://plus.unsplash.com/premium_photo-1723914048561-12a00dd83ec6?q=80&w=1492&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(45.0)
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
					.descripcion("Una noche inolvidable en Barcelona que reunirá a las mejores bandas de rock nacionales e internacionales en un espectáculo cargado de energía, luces y sonido de primer nivel. El evento contará con un escenario de última generación, pantallas gigantes y efectos visuales que acompañarán cada actuación, creando una experiencia inmersiva para todos los asistentes. Durante la velada se podrán disfrutar de clásicos del rock, temas inéditos y colaboraciones sorprendentes entre los artistas invitados. Además, el recinto dispondrá de zonas de descanso, food trucks con propuestas gastronómicas variadas, barras con bebidas exclusivas y espacios para merchandising oficial de las bandas. Será una oportunidad única para vivir la pasión del rock en directo, compartir con otros aficionados y disfrutar de una atmósfera vibrante que solo Barcelona sabe ofrecer. Una cita imprescindible para todos los amantes de la música que buscan emociones intensas y recuerdos imborrables.")
			        .carrusel("https://plus.unsplash.com/premium_photo-1681876467464-33495108737c?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1629276301687-be2af9fd6ba8?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1613423085580-d1b9e13e27b8?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .precio(50.0)
			        .invitados(List.of(DTOInvitado.builder().nombre("Miguel").apellidos("López").fotoURL(
							"https://media.gamestop.com/i/gamestop/11121573_ALT02/PureArts-Cyberpunk2077-Johnny-Silverhand-with-the-RockerBoys-Guitar-Statue?fmt=auto")
							.descripcion("Guitarrista principal").build(),
							DTOInvitado.builder().nombre("Sofía").apellidos("Ramírez").fotoURL(
									"https://tse4.mm.bing.net/th/id/OIP.J6vLzNZP4tL_emdZzg8DDQHaE8?pid=Api&P=0&h=180")
									.descripcion("Baterista").build()))
					.build();

			// Evento 3
			DTOeventoSubida e3 = DTOeventoSubida.builder().nombre("Exposición de Arte Moderno")
					.localizacion("Valencia, España")
					.inicioEvento(LocalDateTime.of(2025, 9, 5, 10, 0))
					.finEvento(LocalDateTime.of(2025, 9, 5, 18, 0))
					.descripcion("Una exposición única en Valencia que reúne a destacados artistas contemporáneos de diferentes disciplinas, incluyendo pintura, escultura, fotografía e instalaciones multimedia. El evento busca ofrecer una experiencia inmersiva que conecta al visitante con las tendencias más innovadoras del arte actual. A lo largo de la muestra, se podrán descubrir obras que exploran temas como la identidad, la tecnología, la sostenibilidad y la interacción humana, generando un espacio de reflexión y diálogo. Además, se organizarán charlas con los propios artistas, talleres participativos y visitas guiadas para profundizar en la comprensión de cada propuesta. La exposición contará con áreas interactivas, zonas de descanso y un ambiente cultural vibrante en el corazón de Valencia, convirtiéndose en un punto de encuentro para amantes del arte, estudiantes y curiosos que buscan inspiración en nuevas formas de expresión creativa.")
					.imagen("https://plus.unsplash.com/premium_photo-1706548911781-dd3ad17a8fa6?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.carrusel("https://images.unsplash.com/photo-1531913764164-f85c52e6e654?q=80&w=1468&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.carrusel("https://images.unsplash.com/photo-1665148553316-0b451be0614f?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.carrusel("https://plus.unsplash.com/premium_photo-1711987692262-0d3aca86fc1d?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1706548911842-7162d4bd2c98?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .precio(13.75)
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
					.descripcion("El torneo más esperado de videojuegos llega a Sevilla para reunir a jugadores profesionales, streamers reconocidos y apasionados del gaming en un evento sin precedentes. Durante varios días, el público podrá disfrutar de intensas competiciones en los títulos más populares del momento, con partidas en vivo transmitidas en pantallas gigantes y narradas por comentaristas expertos. El recinto contará con diferentes zonas temáticas, áreas de juego libre para los asistentes, stands de tecnología y merchandising exclusivo. Además, habrá espacios dedicados a la realidad virtual, competiciones sorpresa, firmas de autógrafos con los creadores de contenido y talleres para quienes deseen mejorar sus habilidades. La atmósfera vibrante de Sevilla se combinará con la pasión gamer para ofrecer una experiencia única que celebrará la cultura de los videojuegos y el entretenimiento digital. Una cita obligatoria para todos los fans del gaming que buscan emoción, comunidad y diversión al máximo nivel.")
			        .carrusel("https://images.unsplash.com/photo-1636487658547-c05ee4cdc1ac?q=80&w=1472&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.carrusel("https://plus.unsplash.com/premium_photo-1683141331949-64810cfc4ca3?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1587095951604-b9d924a3fda0?q=80&w=1632&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1558008258-7ff8888b42b0?q=80&w=1631&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.imagen("https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(25.5)
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
					.descripcion("Un evento deportivo único en Bilbao que combina la pasión por el deporte con un fuerte compromiso social, reuniendo a atletas, aficionados y familias con el objetivo de recaudar fondos para causas solidarias. Durante la jornada se llevarán a cabo diferentes disciplinas, desde carreras populares y torneos de fútbol hasta exhibiciones de deportes urbanos, creando un ambiente inclusivo y dinámico. El evento contará con zonas de animación, actividades para niños, conciertos en vivo y una amplia oferta gastronómica local que resaltará los sabores típicos de la región. Además, se organizarán charlas motivacionales y mesas redondas con deportistas profesionales que compartirán sus experiencias sobre el valor del esfuerzo, el trabajo en equipo y la solidaridad. Todo lo recaudado será destinado a proyectos benéficos, reforzando el espíritu comunitario y la importancia de colaborar por un bien común. Bilbao se convertirá en el epicentro de un día lleno de energía, compañerismo y esperanza, donde el deporte se transforma en una herramienta para inspirar y ayudar.")
			        .carrusel("https://images.unsplash.com/photo-1524646349956-1590eacfa324?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1581889470536-467bdbe30cd0?q=80&w=1564&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.imagen("https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?q=80&w=1474&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(10.5)
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
					.descripcion("Una prueba extrema en los Pirineos que pondrá a prueba los límites de la resistencia física y mental de cada participante. Este desafío combina recorridos de alta montaña con tramos técnicos de escalada, descensos exigentes y etapas acuáticas en ríos de aguas rápidas, creando una experiencia deportiva única en un entorno natural incomparable. Los corredores deberán enfrentarse a cambios de altitud, terrenos irregulares y condiciones climáticas impredecibles, lo que convertirá cada etapa en un verdadero reto de superación personal. Además, el evento contará con zonas de avituallamiento estratégicamente ubicadas, asistencia médica especializada y un equipo de apoyo logístico para garantizar la seguridad de los participantes. El espíritu de la competición no solo reside en la exigencia física, sino también en el compañerismo y la conexión con la naturaleza salvaje de los Pirineos. Una experiencia inolvidable para quienes buscan ir más allá de sus límites y vivir una aventura extrema en uno de los paisajes más impresionantes de Europa.")
			        .carrusel("https://images.unsplash.com/photo-1498581444814-7e44d2fbe0e2?q=80&w=1398&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.carrusel("https://images.unsplash.com/photo-1667205742805-b5154830522b?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1600818596647-9d5318c20a8a?q=80&w=1584&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .imagen("https://plus.unsplash.com/premium_photo-1664301432574-9b4e85c2b2d3?q=80&w=1471&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(35.2)
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
					.descripcion("Una feria literaria en Barcelona que reunirá a escritores consagrados, nuevas voces emergentes, editores, libreros y amantes de los libros en un encuentro cultural sin precedentes. Durante varios días, la ciudad se convertirá en un auténtico punto de referencia para el mundo literario, con presentaciones de novedades editoriales, firmas de ejemplares, conferencias y mesas redondas que abordarán desde la narrativa contemporánea hasta los retos del sector editorial en la era digital. El evento contará también con talleres de escritura creativa, actividades infantiles para despertar la pasión por la lectura desde temprana edad y espacios interactivos que permitirán a los visitantes acercarse de manera directa a los autores. Además, se habilitarán áreas gastronómicas inspiradas en la literatura, rincones de lectura al aire libre y zonas de networking para profesionales del sector. Barcelona, con su tradición cultural y su vibrante vida urbana, ofrecerá el escenario perfecto para una experiencia que celebra la palabra escrita, el diálogo y la creatividad. Una cita imprescindible para quienes entienden que los libros son una ventana al conocimiento y a la imaginación.")
			        .carrusel("https://images.unsplash.com/photo-1544185310-0b3cf501672b?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1718745015015-09cd064a263b?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1660606422784-5a18d4be40fe?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1713720662476-0bf9c2d59308?q=80&w=1402&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.imagen("https://images.unsplash.com/photo-1519682337058-a94d519337bc")
					.precio(15.3)
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
					.descripcion("Una jornada deportiva en Sevilla que reunirá a jóvenes talentos del fútbol en un ambiente cargado de ilusión, compañerismo y espíritu competitivo. El evento se desarrollará en instalaciones de primer nivel y contará con entrenadores profesionales, visores de clubes y expertos en formación deportiva que acompañarán a los participantes durante toda la jornada. Los asistentes podrán disfrutar de partidos amistosos, sesiones de entrenamiento técnico y dinámicas de equipo diseñadas para potenciar las habilidades individuales y el trabajo colectivo. Además, se organizarán actividades paralelas para las familias, con zonas de ocio, espacios gastronómicos y charlas sobre valores como el esfuerzo, la disciplina y la importancia de un estilo de vida saludable. Esta cita busca no solo resaltar el talento emergente del fútbol andaluz, sino también ofrecer un punto de encuentro donde el deporte se convierta en una herramienta de integración y desarrollo personal. Una experiencia única que combina aprendizaje, diversión y la pasión por el fútbol en el corazón de Sevilla.")
			        .carrusel("https://images.unsplash.com/photo-1622659097509-4d56de14539e?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1526232761682-d26e03ac148e?q=80&w=1429&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1664303712613-98938c99f827?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1600077063877-22118d6290eb?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1622659097574-c814ee26068e?q=80&w=1382&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.imagen("https://images.unsplash.com/photo-1598880513655-d1c6d4b2dfbf?q=80&w=1548&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.precio(20.5)
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
					.descripcion("Una conferencia tecnológica en Valencia que reunirá a expertos en innovación, inteligencia artificial y transformación digital para explorar las tendencias que marcarán el futuro de la sociedad y los negocios. Durante varias jornadas, los asistentes podrán participar en ponencias inspiradoras, paneles de debate y demostraciones en vivo de las últimas soluciones tecnológicas aplicadas a sectores como la salud, la educación, la movilidad o las finanzas. El evento contará con la presencia de investigadores, emprendedores, inversores y líderes de la industria que compartirán sus conocimientos y experiencias sobre los retos y oportunidades que ofrece la era digital. Además, habrá talleres prácticos, zonas de networking y espacios interactivos donde los participantes podrán experimentar con herramientas de IA, realidad aumentada y blockchain. Valencia se convertirá así en el epicentro de la innovación, ofreciendo un entorno ideal para generar alianzas estratégicas, descubrir nuevas ideas y anticipar los cambios que transformarán el mundo en los próximos años. Una cita imprescindible para profesionales, estudiantes y entusiastas de la tecnología que buscan estar a la vanguardia del futuro digital.")
			        .carrusel("https://images.unsplash.com/photo-1600320261634-78edd477fa1e?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
					.carrusel("https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?q=80&w=1472&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1683120966127-14162cdd0935?q=80&w=726&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1582192730841-2a682d7375f9?q=80&w=1548&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1581090464777-f3220bbe1b8b?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .precio(35.0)
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
					.descripcion("Un espacio único en Valencia dedicado a explorar lo más reciente y vanguardista del arte contemporáneo, donde artistas locales e internacionales presentan sus obras más innovadoras en pintura, escultura, fotografía, instalaciones y medios digitales. Los visitantes podrán sumergirse en exposiciones interactivas, disfrutar de visitas guiadas, charlas con los propios creadores y talleres participativos que fomentan la creatividad y el diálogo artístico. El evento también contará con zonas de descanso, espacios para el networking cultural y áreas dedicadas a la experimentación tecnológica aplicada al arte. Además, se organizarán actividades especiales para jóvenes y familias, promoviendo la educación artística y la apreciación de nuevas formas de expresión. Valencia se convierte así en un punto de encuentro para amantes del arte, críticos, estudiantes y curiosos que buscan inspirarse y conectar con las tendencias más recientes del panorama contemporáneo. Una experiencia cultural enriquecedora que celebra la creatividad, la innovación y la diversidad artística.")
			        .carrusel("https://images.unsplash.com/photo-1647814568764-3bf5ed6b3c7b?q=80&w=1469&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1675813863340-b7e84c4a1fb0?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://images.unsplash.com/photo-1578163678052-eef169544f75?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .carrusel("https://plus.unsplash.com/premium_photo-1677609898243-63280b6c89a1?q=80&w=766&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
			        .precio(23.4)
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
