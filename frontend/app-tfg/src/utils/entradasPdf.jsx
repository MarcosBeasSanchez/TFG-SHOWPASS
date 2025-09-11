import jsPDF from "jspdf";
import config from '../config/config';


// Función para crear el PDF y devolver el objeto jsPDF
function hacerPDF(ticket) {
    const doc = new jsPDF("portrait", "pt", "a4");
    const margin = 40;
    const contentWidth = 520;

    // Cabecera
    doc.setFillColor(240, 240, 240);
    doc.rect(margin, margin, contentWidth, 60, "F");
    doc.setFont("Helvetica", "bold");
    doc.setTextColor(0, 51, 102);
    doc.text("SHOWPASS", margin + 10, margin + 35);

    // Sección del evento
    doc.setFillColor(0, 51, 102);
    doc.rect(margin, margin + 70, contentWidth, 150, "F");

    if (ticket.eventoImagen) {
        doc.addImage(ticket.eventoImagen, "JPEG", margin + 10, margin + 85, 120, 120);
    }

    doc.setFontSize(18);
    doc.setTextColor(255, 215, 0);
    doc.text(ticket.eventoNombre, margin + 140, margin + 100);

    doc.setFontSize(12);
    doc.setTextColor(255, 255, 255);
    doc.text(`Inicio del evento: ${new Date(ticket.eventoInicio).toLocaleString()}`, margin + 140, margin + 120);
    doc.text(`Fecha de compra: ${new Date(ticket.fechaCompra).toLocaleString()}`, margin + 140, margin + 140);
    doc.text(`Precio: ${ticket.precio.toFixed(2)} €`, margin + 140, margin + 160);

    // QR
    if (ticket.codigoQR) {
        const qrSize = 100;
        doc.addImage(`data:image/png;base64,${ticket.codigoQR}`, "PNG", margin + contentWidth - qrSize - 10, margin + 110, qrSize, qrSize);
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
    doc.text(`Nombre del evento: ${ticket.eventoNombre}`, margin + 10, infoStartY + 80);
    doc.text(`Inicio del evento: ${new Date(ticket.eventoInicio).toLocaleString()}`, margin + 10, infoStartY + 100);

    doc.setFontSize(10);
    doc.setTextColor(120, 120, 120);
    doc.text("Prohibida la reventa. No reembolsable. Mostrar en la entrada del evento.", margin + 10, infoStartY + 130);

    return doc;
}
// Descargar PDF
function descargarPDF(ticket) {
    const doc = hacerPDF(ticket);
    doc.save(`ticket_${ticket.id}_${ticket.eventoNombre}.pdf`);
}
// Función para enviar el PDF al correo del usuario
async function enviarPDF(ticket) {
    try {
        const user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
        const email = user?.email;
        if (!email) {
            alert("No se encontró el email del usuario.");
            return;
        }
        const doc = hacerPDF(ticket);
        const pdfBase64 = doc.output("datauristring").split(',')[1];

        const response = await fetch(`${config.apiBaseUrl}/tfg/carrito/enviarPdfEmail`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email,
                ticketId: ticket.id,
                eventoNombre: ticket.eventoNombre,
                pdfBase64
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