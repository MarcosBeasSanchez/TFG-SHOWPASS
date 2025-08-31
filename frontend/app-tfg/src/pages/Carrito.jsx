import { useEffect, useState } from "react";
import config from "../config/config";
import jsPDF from "jspdf";


export default function ShoppingCart({ user }) {
  const [carrito, setCarrito] = useState(null);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [ticketsComprados, setTicketsComprados] = useState([]);
  const [totalCompra, setTotalCompra] = useState([])
  const [reverseOrder, setReverseOrder] = useState(true); // NUEVA VARIABLE


  //cogemos el id del usuario desde localStorage
  const usuarioId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;

  useEffect(() => {
    if (!usuarioId) return;

    const fetchCarrito = async () => {
      try {
        setLoading(true);
        const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/${usuarioId}`);
        if (!res.ok) throw new Error("No se pudo cargar el carrito");
        console.log("Carrito:", res);
        const data = await res.json();
        setCarrito(data);

        // Calcular total
        const totalRes = await fetch(`${config.apiBaseUrl}/tfg/carrito/total/${usuarioId}`);
        const totalData = await totalRes.json();
        setTotal(totalData);
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCarrito();
  }, [usuarioId]);

  const eliminarEvento = async (eventoId) => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/eliminar/${usuarioId}/${eventoId}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error("Error eliminando evento");
      const data = await res.json();
      setCarrito(data);

      // Actualizar total
      const totalRes = await fetch(`${config.apiBaseUrl}/tfg/carrito/total/${usuarioId}`);
      const totalData = await totalRes.json();
      setTotal(totalData);
    } catch (err) {
      console.error(err);
      alert("No se pudo eliminar el evento");
    }
  };

  const vaciarCarrito = async () => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/vaciar/${usuarioId}`,
        { method: "DELETE" });
      if (!res.ok) throw new Error("Error vaciando carrito");
      const data = await res.json();
      setCarrito(data);
      setTotal(0);
    } catch (err) {
      console.error(err);
      alert("No se pudo vaciar el carrito");
    }
  };

  const finalizarCompra = async () => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/finalizar/${usuarioId}`,
        {
          method: "POST"
        });

      if (!res.ok) throw new Error("Error al finalizar la compra");

      const ticketRes = await fetch(`${config.apiBaseUrl}/tfg/ticket/findByUsuarioId/${usuarioId}`)
      const tickts = await ticketRes.json();

      setTicketsComprados(tickts)

      const total = tickts.reduce((acc, ticket) => acc + ticket.precio, 0);
      setTotalCompra(total)


      alert("Compra realizada con éxito!");
    } catch (err) {
      console.error(err);
      alert("No se pudo finalizar la compra");
    }
  };


  const descargarPDF = (ticket) => {
    const doc = new jsPDF("portrait", "pt", "a4"); // Tamaño A4, puntos

    const margin = 40;
    const contentWidth = 520;

    // --- Cabecera ---
    doc.setFillColor(240, 240, 240); // fondo gris claro
    doc.rect(margin, margin, contentWidth, 60, "F"); // rectángulo relleno



    // Nombre de la página y icono
    doc.setFontSize(22);
    doc.setFont("helvetica", "bold");
    doc.setTextColor(0, 51, 102); // azul oscuro
    doc.text("SHOWPASS", margin + 70, margin + 35);
    // Puedes agregar un icono como imagen si lo tienes

    // --- Sección del evento ---
    doc.setFillColor(0, 51, 102); // azul oscuro
    doc.rect(margin, margin + 70, contentWidth, 150, "F");

    // Imagen del evento
    if (ticket.eventoImagen) {
      doc.addImage(ticket.eventoImagen, "JPEG", margin + 10, margin + 80, 120, 120);
    }

    // Nombre del evento
    doc.setFontSize(18);
    doc.setTextColor(255, 215, 0); // dorado
    doc.text(ticket.eventoNombre, margin + 140, margin + 100);

    // Fecha de inicio
    doc.setFontSize(12);
    doc.setTextColor(255, 255, 255);
    doc.text(`Inicio: ${new Date(ticket.eventoInicio).toLocaleString()}`, margin + 140, margin + 120);

    // Precio
    doc.text(`Precio: ${ticket.precio.toFixed(2)} €`, margin + 140, margin + 140);

    // --- QR en la esquina derecha ---
    if (ticket.codigoQR) {
      const qrSize = 100;
      doc.addImage(`data:image/png;base64,${ticket.codigoQR}`, "PNG", margin + contentWidth - qrSize - 10, margin + 80, qrSize, qrSize);
    }

    // --- Información del usuario debajo de la sección ---
    const infoStartY = margin + 250;
    doc.setFillColor(245, 245, 245); // gris claro
    doc.rect(margin, infoStartY, contentWidth, 100, "F");

    doc.setFontSize(12);
    doc.setTextColor(0, 0, 0);
    doc.text(`ID Ticket: ${ticket.id}`, margin + 10, infoStartY + 20);
    doc.text(`Usuario ID: ${ticket.usuarioId}`, margin + 10, infoStartY + 40);
    doc.text(`Evento ID: ${ticket.eventoId}`, margin + 10, infoStartY + 60);

    // Nota inferior
    doc.setFontSize(10);
    doc.setTextColor(120, 120, 120);
    doc.text("Prohibida la reventa. No reembolsable. Mostrar en la entrada del evento.", margin + 10, infoStartY + 90);

    doc.save(`ticket_${ticket.id}.pdf`);
  };

  {/*if (loading) return <p className="p-4">Cargando carrito...</p>;*/ }
  if (error) return <p className="p-4 text-red-500">Error: {error}</p>;
  if (!carrito || carrito.eventos.length === 0)
    return (
      <div className="flex flex-col items-center justify-center min-h-screen w-full px-4">
        <div className="bg-white shadow-lg rounded-lg p-6 sm:p-8 flex flex-col items-center w-full max-w-md">
          <span className="material-symbols-outlined text-5xl sm:text-6xl text-gray-400 mb-4">
            shopping_cart_off
          </span>
          <h2 className="text-xl sm:text-2xl font-bold text-gray-700 mb-2 text-center">Tu carrito está vacío</h2>
          <p className="text-gray-500 mb-4 text-center">¡Añade eventos para empezar tu compra!</p>
          <img
            src="https://i.giphy.com/giXLnhxp60zEEIkq8K.webp"
            alt="vacio..."
            className="w-40 h-40 sm:w-60 sm:h-60 mb-6 object-contain"
          />
          <a
            href="/"
            className="bg-blue-500 text-white px-4 sm:px-6 py-2 rounded-lg hover:bg-blue-600 transition text-center w-full"
          >
            Ir a eventos
          </a>
        </div>
      </div>
    );

  return (
    <div className="max-w-5xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-blue-950">Tu Carrito de Compras</h1>

      <div className="space-y-4">
        {carrito.eventos.map((evento, index) => (
          <div key={`${evento.id}-${index}`} className="flex items-center justify-between pr-5 bg-white shadow border-gray-500 border-l-4 p-2 ">
            <div className="flex items-center gap-4">
              <img src={evento.imagen} alt={evento.nombre} className="w-30 h-30 object-cover" />
              <div className="flex-3">
                <h2 className="text-xl font-semibold text-blue-950">{evento.nombre}</h2>
                <p className="text-gray-600">{evento.localizacion}</p>
                <p className="text-gray-500">{evento.precio.toFixed(2)} €</p>
              </div>
            </div>
            <button
              onClick={() => eliminarEvento(evento.id)}
              className="bg-red-500 text-white px-4 py-2 text-xs rounded hover:bg-red-600"
            >
              Eliminar
            </button>
          </div>
        ))}
      </div>

      <div className="mt-6 flex justify-between items-center">
        <div className="flex gap-4">
          <button
            onClick={vaciarCarrito}
            className="bg-gray-500 text-white px-4 py-2  text-sm rounded hover:bg-gray-600"
          >
            Vaciar carrito
          </button>
          <button
            onClick={finalizarCompra}
            className="bg-green-500 text-white  p-1 text-sm py-2 rounded hover:bg-green-600 "
          >
            Comprar
          </button>
        </div>
        <p className="text-xl font-bold pl-2  text-blue-950 ">Total: {total.toFixed(2)}€</p>

      </div>

      {/* Resumen tickets comprados */}
      {ticketsComprados.length > 0 && (
        <div className="mt-6 p-4 bg-white shadow rounded">
          <h2 className="text-2xl font-semibold text-gray-500 mb-4">Resumen de tu compra</h2>
          <div className="w-full flex justify-end">
            <button
              onClick={() => setReverseOrder((prev) => !prev)}
              className="mb-4 bg-gray-200 text-gray-700 px-3 py-1 rounded hover:bg-gray-300 text-xs text-right"
            >
              Cambiar orden: {reverseOrder ? "Recientes" : "Antiguos"}
            </button>
          </div>
          <ul className={`flex ${reverseOrder ? "flex-col-reverse" : "flex-col"}`}>
            {ticketsComprados.map((ticket) => (
              <li key={ticket.id} className="flex md:flex-row flex-col justify-between  items-center border p-2 rounded gap-2">
                <div className="flex items-center gap-4">
                  <span className="material-symbols-outlined text-gray-400" style={{ fontSize: "60px" }}>
                    qr_code
                  </span>

                  <div>
                    <p className="font-semibold text-blue-950">{ticket.eventoNombre}</p>
                    <p className="text-gray-500">Precio: {ticket.precio.toFixed(2)} €</p>
                    <p className="text-gray-500">Inicio del evento: {new Date(ticket.eventoInicio).toLocaleString()}</p>
                  </div>
                </div>
                <div className="text-end">
                  <button
                    onClick={() => descargarPDF(ticket)}
                    className="bg-blue-500 text-white text-xs  px-3 py-1 rounded hover:bg-blue-600"
                  >
                    Descargar PDF
                  </button>
                </div>

              </li>
            ))}
          </ul>
          <p className="mt-4 font-bold text-xl">Total a pagar: {totalCompra.toFixed(2)} €</p>
        </div>
      )}

    </div>
  );
}