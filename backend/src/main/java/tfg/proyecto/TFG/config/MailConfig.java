package tfg.proyecto.TFG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuración de Spring para el envío de correos electrónicos mediante JavaMail.
 * <p>
 * Proporciona un bean {@link JavaMailSender} que puede ser inyectado en servicios para
 * enviar correos con SMTP (en este caso, Gmail).
 */
@Configuration
public class MailConfig {
	
	/**
     * Crea y configura un {@link JavaMailSender} listo para enviar emails.
     * <p>
     * Configuración:
     * <ul>
     *     <li>Host SMTP de Gmail: smtp.gmail.com</li>
     *     <li>Puerto: 587 (STARTTLS)</li>
     *     <li>Usuario y contraseña del remitente</li>
     *     <li>Propiedades de autenticación, protocolo y depuración</li>
     * </ul>
     *
     * @return Bean {@link JavaMailSender} para enviar correos
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Configuración básica del servidor SMTP
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("showpasstfg@gmail.com");
        mailSender.setPassword("ttvh gkxj ucga jluu"); // Contraseña de aplicación Gmail

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // Protocolo SMTP
        props.put("mail.smtp.auth", "true");  // Autenticación requerida
        props.put("mail.smtp.starttls.enable", "true"); // Habilitar STARTTLS
        props.put("mail.debug", "true"); // Activar logs de depuración

        return mailSender;
    }
}