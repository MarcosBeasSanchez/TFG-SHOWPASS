package tfg.proyecto.TFG.servicios;

import java.io.File;

import jakarta.mail.MessagingException;

public interface IServicioPdfEmail {
	
    void sendPdfEmail(String toEmail, String subject, String body, File pdfFile) throws MessagingException;



}
