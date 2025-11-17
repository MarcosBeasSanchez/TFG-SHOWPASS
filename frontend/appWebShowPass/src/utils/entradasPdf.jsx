import jsPDF from "jspdf";
import config from '../config/config';
import { useEffect } from "react";


 // Función para detectar si la imagen es URL o Base64
  const getImageSrc = (img) => {
    if (!img) return null; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };


async function loadImageAsBase64(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error("No se pudo cargar la imagen");
    const blob = await response.blob();
    return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onloadend = () => resolve(reader.result);
        reader.readAsDataURL(blob);
    });
}

// Función para crear el PDF y devolver el objeto jsPDF
async function hacerPDF(ticket, evento, ticketQR) {
    const doc = new jsPDF("portrait", "pt", "a4");
    const margin = 40;
    const contentWidth = 520;

    // Cabecera
    doc.setFillColor(240, 240, 240);
    doc.rect(margin, margin, contentWidth, 100, "F");
    doc.setFont("Helvetica", "bold");
    doc.setFontSize(24);
    doc.setTextColor(0, 51, 102);
    doc.text("SHOWPASS", margin + 10, margin + 35);

    // Sección del evento
    doc.setFillColor(0, 51, 102);
    doc.rect(margin, margin + 70, contentWidth, 150, "F");

    // Imagen del evento
    if (evento.imagenPrincipalUrl) {
        doc.addImage(getImageSrc(evento.imagenPrincipalUrl) , "JPEG", margin + 10, margin + 85, 120, 120);
    }else{
        const placeholder = await loadImageAsBase64('https://via.placeholder.com/120x120.png?text=No+Image');
        doc.addImage(placeholder, "PNG", margin + 10, margin + 85, 120, 120);
    }

    doc.setFontSize(18);
    doc.setTextColor(255, 215, 0);
    doc.text(evento.nombre, margin + 140, margin + 100);

    doc.setFontSize(12);
    doc.setTextColor(255, 255, 255);
    doc.text(`Inicio del evento: ${new Date(evento.inicioEvento).toLocaleString()}`, margin + 140, margin + 120);
    doc.text(`Fecha de compra: ${new Date(evento.finEvento).toLocaleString()}`, margin + 140, margin + 140);
    doc.text(`Precio: ${evento.precio.toFixed(2)} €`, margin + 140, margin + 160);

    // QR
    if (ticketQR) {
        const qrSize = 100;
        // Construimos la URL completa del QR (según tu servidor)
        try {
            const qrBase64 = await loadImageAsBase64(ticketQR);
            doc.addImage(
                qrBase64,
                "PNG",
                margin + contentWidth - qrSize - 10,
                margin + 110,
                qrSize,
                qrSize
            );
        } catch (err) {
            console.error("Error cargando el QR:", err);
        }
    }

    // Información del usuario
    const infoStartY = margin + 250;
    doc.setFillColor(245, 245, 245);
    doc.rect(margin, infoStartY, contentWidth, 500, "F");

    doc.setFontSize(12);
    doc.setTextColor(0, 0, 0);
    doc.text(`ID Ticket: ${ticket.id}`, margin + 10, infoStartY + 20);
    doc.text(`Usuario ID: ${ticket.usuarioId}`, margin + 10, infoStartY + 40);
    doc.text(`Evento ID: ${ticket.eventoId}`, margin + 10, infoStartY + 60);
    doc.text(`Nombre del evento: ${ticket.nombreEvento}`, margin + 10, infoStartY + 80);
    doc.text(`Inicio del evento: ${new Date(evento.inicioEvento).toLocaleString()}`, margin + 10, infoStartY + 100);

    doc.setFontSize(10);
    doc.setTextColor(120, 120, 120);
    doc.text("Prohibida la reventa. No reembolsable. Mostrar en la entrada del evento.", margin + 10, infoStartY + 130);

    return doc;
}
// Descargar PDF
async function descargarPDF(ticket) {
    try {
        // Esperar la respuesta correctamente
        const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findById?id=${ticket.eventoId}`);
        if (!res.ok) throw new Error("Error obteniendo el evento del backend");
        const evento = await res.json();
        ticket.evento = evento;
        console.log("Evento recibido del backend para PDF:", evento);

        const qr = await fetch(`${config.apiBaseUrl}/tfg/ticket/${ticket.id}/qr`);
        if (!qr.ok) throw new Error("Error obteniendo el código QR del backend");
        const qrData = await qr.json();
        ticket.codigoQR = qrData.codigoQR;
        ticket.contenidoQR = qrData.contenidoQR;
        console.log("Código QR recibido del backend para PDF:", ticket.codigoQR, ticket.contenidoQR);


        // Crear y guardar el PDF con los datos del evento
        const doc = await hacerPDF(ticket, ticket.evento, ticket.codigoQR);
        doc.save(`ticket_${ticket.id}_${ticket.nombreEvento}.pdf`);
    } catch (error) {
        console.error("Error generando el PDF:", error);
        alert("Error al generar el PDF.");
    }
}

// Función para enviar el PDF al correo del usuario
// Función para enviar el PDF al correo del usuario
async function enviarPDF(ticket) {
    try {
        // Obtener el email del usuario
        const user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
        const email = user?.email;
        if (!email) {
            alert("No se encontró el email del usuario.");
            return;
        }

        // Si ticket.evento no existe, hacemos fetch
        if (!ticket.evento) {
            const resEvento = await fetch(`${config.apiBaseUrl}/tfg/evento/findById?id=${ticket.eventoId}`);
            if (!resEvento.ok) throw new Error("Error obteniendo el evento del backend");
            ticket.evento = await resEvento.json();
        }

        // Si ticket.codigoQR no existe, hacemos fetch
        if (!ticket.codigoQR) {
            const resQR = await fetch(`${config.apiBaseUrl}/tfg/ticket/${ticket.id}/qr`);
            if (!resQR.ok) throw new Error("Error obteniendo el código QR del backend");
            const qrData = await resQR.json();
            ticket.codigoQR = qrData.codigoQR;
        }

        console.log("Evento para PDF:", ticket.evento);
        console.log("QR para PDF:", ticket.codigoQR);

        // Generar PDF usando los datos cargados
        const doc = await hacerPDF(ticket, ticket.evento, ticket.codigoQR);

        // Convertir a Base64
        const pdfBase64 = doc.output("datauristring").split(',')[1];

        // Enviar al backend
        const response = await fetch(`${config.apiBaseUrl}/tfg/utilidades/enviarPdfEmail`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            //crear JSON
            body: JSON.stringify({
                email : email,
                ticketId: ticket.id,
                eventoNombre: ticket.nombreEvento,
                pdfBase64 //pdf en base64
            })
        });

        if (response.ok) {
            alert("PDF enviado a tu correo electrónico: " + email);
        } else {
            const errorText = await response.text();
            console.error("Error del backend:", errorText);
            alert("Error al enviar el PDF por correo.");
        }
    } catch (error) {
        console.error("Error enviando el PDF por correo:", error);
        alert("Error al enviar el PDF por correo.");
    }
}

export { hacerPDF, descargarPDF, enviarPDF };