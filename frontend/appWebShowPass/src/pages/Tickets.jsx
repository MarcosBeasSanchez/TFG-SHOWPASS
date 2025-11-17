import React, { useEffect, useState } from 'react';
import jsPDF from "jspdf";
import config from '../config/config';
import ticketSVG from '../assets/TICKETSVG.svg';
import { descargarPDF, enviarPDF } from "../utils/entradasPdf";



const UserTickets = () => {
  const [tickets, setTickets] = useState([]);
  const [reverseOrder, setReverseOrder] = useState(true); // NUEVA VARIABLE
  const userId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;


  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const response = await fetch(`${config.apiBaseUrl}/tfg/ticket/findByUsuarioId/${userId}`);
        if (response.ok) {
          const data = await response.json();
          console.log('Tickets recibidos del backend:', data);
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

  const getEstadoColorClass = (estado) => {
    switch (estado) {
        case 'VALIDO':
            return 'text-green-600 font-semibold'; // Verde
        case 'USADO':
            return 'text-orange-500 font-semibold'; // Naranja
        case 'ANULADO':
            return 'text-red-600 font-semibold';   // Rojo
        default:
            return 'text-gray-500'; // Color por defecto (si el estado no coincide)
    }
};



  return (
    <div className="max-w-5xl mx-auto p-6 md-6  ">
      <div className='p-10 bg-white shadow rounded oscuro'>

        <h2 className="text-2xl font-bold mb-4 text-gray-500 oscuroTextoGris">Tus Tickets</h2>
        <div className="w-full flex justify-end">
          <button
            onClick={() => setReverseOrder((prev) => !prev)}
            className="mb-4 bg-gray-200 text-gray-700 px-3 py-1 rounded hover:bg-gray-300 text-xs text-right oscuroBtn"
          >
            Cambiar orden: {reverseOrder ? "Recientes" : "Antiguos"}
          </button>
        </div>
        

        <ul className={`flex ${reverseOrder ? "flex-col-reverse" : "flex-col"} gap-2`}>
          {tickets.length > 0 ? tickets.map((ticket) => (
            <li key={ticket.id} className="flex md:justify-between border-none p-2 rounded flex-col md:flex-row gap-2 border- ">
              <div className="flex items-center gap-4">
                <span className="material-symbols-outlined text-gray-400 " style={{ fontSize: "60px" }}>
                  qr_code
                </span>
                <div>
                  <p className="font-semibold text-blue-950 oscuroTextoGris">{ticket.nombreEvento}</p>
                  <p className="text-gray-500">Precio: {ticket.precioPagado?.toFixed(2) ?? "?"} €</p>
                  <p className="text-gray-500">Fecha Compra: {new Date(ticket.fechaCompra).toLocaleString()}</p>
                  <p className={getEstadoColorClass(ticket.estado)}>
                    <span className="text-gray-500">Estado: </span>{ticket.estado}</p>
                </div>
              </div>

              <div className='gap-2 flex md:flex-col w-auto flex-row'>
                <button
                  onClick={() => descargarPDF(ticket)}
                  className="bg-blue-500 text-white  text-sm px-3 py-1 rounded hover:bg-blue-600"
                >
                  Descargar PDF
                </button>

                <button
                  onClick={() => enviarPDF(ticket)}
                  className="bg-gray-500 text-white text-sm px-3 py-1 rounded hover:bg-gray-600"
                >
                  Enviar PDF a mi email
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