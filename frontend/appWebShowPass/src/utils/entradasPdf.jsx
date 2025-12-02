// ----------------------------------------------------
// 1. IMPORTS
// ----------------------------------------------------
// Importa la librería jsPDF, utilizada para generar documentos PDF desde JavaScript en el cliente.
import jsPDF from "jspdf";
// Importa la configuración de la aplicación, probablemente conteniendo la URL base de la API.
import config from '../config/config';


// ----------------------------------------------------
// 2. FUNCIÓN AUXILIAR: DETERMINAR FUENTE DE IMAGEN
// ----------------------------------------------------
// determina cómo interpretar y construir la URL o Data URI de una imagen.
  const getImageSrc = (img) => {
    if (!img) return null; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };

// ----------------------------------------------------
// 3. FUNCIÓN AUXILIAR: CARGAR IMAGEN A BASE64
// ----------------------------------------------------
// Función asíncrona clave para cargar una imagen (desde una URL) y convertirla
// a formato Base64 (Data URI), que es el formato que jsPDF requiere para incrustar imágenes.
async function loadImageAsBase64(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error("No se pudo cargar la imagen");
    const blob = await response.blob(); // Obtiene la imagen como Blob
    
    // Usa FileReader para la conversión asíncrona de Blob a Data URI (Base64)
    return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onloadend = () => resolve(reader.result); // Resuelve con la cadena Base64
        reader.readAsDataURL(blob);
    });
}

// ----------------------------------------------------
// 4. FUNCIÓN CENTRAL: GENERAR ESTRUCTURA DEL PDF
// ----------------------------------------------------
// Crea el documento PDF con el diseño del ticket, usando los datos del ticket, el evento y el QR.
async function hacerPDF(ticket, evento, ticketQR) {
    // Inicializa jsPDF con orientación vertical (portrait), unidades en puntos (pt) y formato A4.
    const doc = new jsPDF("portrait", "pt", "a4");
    const margin = 40;
    const contentWidth = 520; // Ancho del contenido (A4 es 595pt de ancho)

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

    // Lógica de la Imagen del evento
    if (evento.imagenPrincipalUrl) {
        // Usa la función getImageSrc para obtener la fuente y la añade al PDF.
        doc.addImage(getImageSrc(evento.imagenPrincipalUrl) , "JPEG", margin + 10, margin + 85, 120, 120);
    }else{
        // Si no hay imagen, carga un placeholder (desde una URL externa).
        const placeholder = await loadImageAsBase64('https://via.placeholder.com/120x120.png?text=No+Image');
        doc.addImage(placeholder, "PNG", margin + 10, margin + 85, 120, 120);
    }
    // Título y detalles del evento
    doc.setFontSize(18);
    doc.setTextColor(255, 215, 0);
    doc.text(evento.nombre, margin + 140, margin + 100);
    doc.setFontSize(12);
    doc.setTextColor(255, 255, 255); 
    doc.text(`Inicio del evento: ${new Date(evento.inicioEvento).toLocaleString()}`, margin + 140, margin + 120);
    doc.text(`Fecha de compra: ${new Date(evento.finEvento).toLocaleString()}`, margin + 140, margin + 140);
    doc.text(`Precio: ${evento.precio.toFixed(2)} €`, margin + 140, margin + 160);

   // Lógica del Código QR
    if (ticketQR) {
        const qrSize = 100;
        // Carga el QR (que es una URL a la imagen del QR) y lo convierte a Base64
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
    
    // Nota legal
    doc.setFontSize(10);
    doc.setTextColor(120, 120, 120);
    doc.text("Prohibida la reventa. No reembolsable. Mostrar en la entrada del evento.", margin + 10, infoStartY + 130);

    return doc;
}
// Descargar PDF


// ----------------------------------------------------
// 5. FUNCIÓN EXPORTABLE: DESCARGAR PDF
// ----------------------------------------------------
// Orquesta la obtención de datos y la descarga del PDF en el navegador.
async function descargarPDF(ticket) {
    try {
       // 1. Obtener datos del Evento
        const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findById?id=${ticket.eventoId}`);
        if (!res.ok) throw new Error("Error obteniendo el evento del backend");
        const evento = await res.json();
        ticket.evento = evento;
        console.log("Evento recibido del backend para PDF:", evento);

        // 2. Obtener datos del Código QR
        const qr = await fetch(`${config.apiBaseUrl}/tfg/ticket/${ticket.id}/qr`);
        if (!qr.ok) throw new Error("Error obteniendo el código QR del backend");
        const qrData = await qr.json();
        ticket.codigoQR = qrData.codigoQR;
        ticket.contenidoQR = qrData.contenidoQR;
        console.log("Código QR recibido del backend para PDF:", ticket.codigoQR, ticket.contenidoQR);
        
        // 3. Crear y guardar el PDF
        const doc = await hacerPDF(ticket, ticket.evento, ticket.codigoQR);
        // Utiliza el método nativo de jsPDF para forzar la descarga en el navegador.
        doc.save(`ticket_${ticket.id}_${ticket.nombreEvento}.pdf`);
    } catch (error) {
        console.error("Error generando el PDF:", error);
        alert("Error al generar el PDF.");
    }
}

// ----------------------------------------------------
// 6. ENVIAR PDF POR EMAIL
// ----------------------------------------------------
// Orquesta la obtención de datos, la generación del PDF y su envío al backend para que el servidor lo envíe por email.
async function enviarPDF(ticket) {
    try {
        // 1. Obtener el email del usuario desde el localStorage
        const user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
        const email = user?.email;
        if (!email) {
            alert("No se encontró el email del usuario.");
            return;
        }

       // 2. Obtener datos faltantes (Evento y QR), si no están ya en el objeto ticket
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

        // 3. Generar PDF
        const doc = await hacerPDF(ticket, ticket.evento, ticket.codigoQR);

        // 4. Convertir el PDF generado a Base64 para enviarlo en el cuerpo de la solicitud POST.
        const pdfBase64 = doc.output("datauristring").split(',')[1];

        // 5. Enviar el Base64 del PDF al backend para que lo adjunte y envíe por email.
        const response = await fetch(`${config.apiBaseUrl}/tfg/utilidades/enviarPdfEmail`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            //crear JSON
            body: JSON.stringify({
                email : email, // email del usuario
                ticketId: ticket.id, // ID del ticket
                eventoNombre: ticket.nombreEvento, //nombre del evento
                pdfBase64 //pdf en base64
            })
        });

        //Respuesta del backend
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


// ----------------------------------------------------
// 7. EXPORTACIONES
// ----------------------------------------------------
// Exporta las funciones para que puedan ser utilizadas por otros componentes (ej. el componente de la lista de tickets).
export { hacerPDF, descargarPDF, enviarPDF };