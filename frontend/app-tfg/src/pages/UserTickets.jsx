import React, { useEffect, useState } from 'react';
import jsPDF from "jspdf";
import config from '../config/config';
import ticketSVG from '../assets/TICKETSVG.svg';



const UserTickets = () => {
  const [tickets, setTickets] = useState([]);
  const svgBase64 = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjRweCIgdmlld0JveD0iMCAtOTYwIDk2MCA5NjAiIHdpZHRoPSIyNHB4IiBmaWxsPSIjRkZGRkZGIj48cGF0aCBkPSJtMzY4LTMyMCAxMTItODQgMTEwIDg0LTQyLTEzNiAxMTItODhINTI0bC00NC0xMzYtNDQgMTM2SDMwMGwxMTAgODgtNDIgMTM2Wk0xNjAtMTYwcS0zMyAwLTU2LjUtMjMuNVQ4MC0yNDB2LTEzNXEwLTExIDctMTl0MTgtMTBxMjQtOCAzOS41LTI5dDE1LjUtNDdxMC0yNi0xNS41LTQ3VDEwNS01NTZxLTExLTItMTgtMTB0LTctMTl2LTEzNXEwLTMzIDIzLjUtNTYuNVQxNjAtODAwaDY0MHEzMyAwIDU2LjUgMjMuNVQ4ODAtNzIwdjEzNXEwIDExLTcgMTl0LTE4IDEwcS0yNCA4LTM5LjUgMjlUODAwLTQ4MHEwIDI2IDE1LjUgNDd0MzkuNSAyOXExMSAyIDE4IDEwdDcgMTl2MTM1cTAgMzMtMjMuNSA1Ni41VDgwMC0xNjBIMTYwWm0wLTgwaDY0MHYtMTAycS0zNy0yMi01OC41LTU4LjVUNzIwLTQ4MHEwLTQzIDIxLjUtNzkuNVQ4MDAtNjE4di0xMDJIMTYwdjEwMnEzNyAyMiA1OC41IDU4LjVUMjQwLTQ4MHEwIDQzLTIxLjUgNzkuNVQxNjAtMzQydjEwMlptMzIwLTI0MFoiLz48L3N2Zz4="; 

  const [reverseOrder, setReverseOrder] = useState(true); // NUEVA VARIABLE
  const userId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;


  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const response = await fetch(`${config.apiBaseUrl}/tfg/ticket/findByUsuarioId/${userId}`);
        if (response.ok) {
          const data = await response.json();
          setTickets(data);
        } else {

          console.error('Error fetching tickets');
        }
      } catch (error) {
        console.error('Fetch error:', error);
      }
    };

    if (userId) {
      fetchTickets();
    }
  }, [userId]);

  // Ejemplo de función para descargar PDF
const descargarPDF = (ticket) => {
  const doc = new jsPDF("portrait", "pt", "a4"); // Tamaño A4, puntos
  const margin = 40;
  const contentWidth = 520;

  // --- Cabecera ---
  doc.setFillColor(240, 240, 240); // fondo gris claro
  doc.rect(margin, margin, contentWidth, 60, "F"); // rectángulo relleno

  // Nombre de la página y icono
  //doc.addImage(svgBase64, "SVG", margin + 20, margin + 15, 30, 30);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(0, 51, 102); // azul oscuro
  doc.text("SHOWPASS", margin + 70, margin + 35);

  // --- Sección del evento ---
  doc.setFillColor(0, 51, 102); // azul oscuro
  doc.rect(margin, margin + 70, contentWidth, 150, "F");


  // Imagen del evento
  if (ticket.eventoImagen) {
    doc.addImage(ticket.eventoImagen, "JPEG", margin + 10, margin + 85, 120, 120);
  }

  // Nombre del evento
  doc.setFontSize(18);
  doc.setTextColor(255, 215, 0); // dorado
  doc.text(ticket.eventoNombre, margin + 140, margin + 100);

  // Fecha de inicio del evento
  doc.setFontSize(12);
  doc.setTextColor(255, 255, 255);
  doc.text(`Inicio del evento: ${new Date(ticket.eventoInicio).toLocaleString()}`, margin + 140, margin + 120);

  // Fecha de compra
  doc.text(`Fecha de compra: ${new Date(ticket.fechaCompra).toLocaleString()}`, margin + 140, margin + 140);

  // Precio
  doc.text(`Precio: ${ticket.precio.toFixed(2)} €`, margin + 140, margin + 160);

  // --- QR en la esquina derecha ---
  if (ticket.codigoQR) {
    const qrSize = 100;
    doc.addImage(`data:image/png;base64,${ticket.codigoQR}`, "PNG", margin + contentWidth - qrSize - 10, margin + 110, qrSize, qrSize);
  }

  // --- Información del usuario debajo de la sección ---
  const infoStartY = margin + 250;
  doc.setFillColor(245, 245, 245); // gris claro
  doc.rect(margin, infoStartY, contentWidth, 500, "F");

  doc.setFontSize(12);
  doc.setTextColor(0, 0, 0);
  doc.text(`ID Ticket: ${ticket.id}`, margin + 10, infoStartY + 20);
  doc.text(`Usuario ID: ${ticket.usuarioId}`, margin + 10, infoStartY + 40);
  doc.text(`Evento ID: ${ticket.eventoId}`, margin + 10, infoStartY + 60);
  doc.text(`Nombre del evento: ${ticket.eventoNombre}`, margin + 10, infoStartY + 80);
  doc.text(`Inicio del evento: ${new Date(ticket.eventoInicio).toLocaleString()}`, margin + 10, infoStartY + 100);

  // Nota inferior
  doc.setFontSize(10);
  doc.setTextColor(120, 120, 120);
  doc.text("Prohibida la reventa. No reembolsable. Mostrar en la entrada del evento.", margin + 10, infoStartY + 130);

  doc.save(`ticket_${ticket.id}${ticket.eventoNombre}.pdf`);
};

  return (
    <div className="mt-6 p-5 ">
      <div className='p-10 bg-white shadow rounded'>
        <h2 className="text-2xl font-bold mb-4 text-gray-500">Tus Tickets</h2>
        <div className="w-full flex justify-end">
          <button
            onClick={() => setReverseOrder((prev) => !prev)}
            className="mb-4 bg-gray-200 text-gray-700 px-3 py-1 rounded hover:bg-gray-300 text-xs text-right"
          >
            Cambiar orden: {reverseOrder ? "Recientes" : "Antiguos"}
          </button>
        </div>

        <ul className={`flex ${reverseOrder ? "flex-col-reverse" : "flex-col"} gap-2`}>
          {tickets.length > 0 ? tickets.map((ticket) => (
            <li key={ticket.id} className="flex md:justify-between border p-2 rounded flex-col md:flex-row gap-2">
              <div className="flex items-center gap-4">
                <span className="material-symbols-outlined text-gray-400" style={{ fontSize: "60px" }}>
                  qr_code
                </span>
                <div>
                  <p className="font-semibold text-blue-950">{ticket.eventoNombre}</p>
                  <p className="text-gray-500">Precio: {ticket.precio?.toFixed(2) ?? "?"} €</p>
                  <p className="text-gray-500">Fecha Compra: {new Date(ticket.fechaCompra).toLocaleString()}</p>
                </div>
              </div>
              <div className='text-end'>
                <button
                  onClick={() => descargarPDF(ticket)}
                  className="bg-blue-500 text-white text-sm px-3 py-1 rounded hover:bg-blue-600"
                >
                  Descargar PDF
                </button>
              </div>
            </li>
          )) : (
            <li className="text-gray-500 text-center py-8">No tienes tickets comprados.</li>
          )}
        </ul>
      </div>
    </div>
  );
};
export default UserTickets;