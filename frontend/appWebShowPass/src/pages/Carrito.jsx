import { useEffect, useState } from "react";
import config from "../config/config";
import jsPDF from "jspdf";
import { descargarPDF, enviarPDF } from "../utils/entradasPdf";


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
  const carritoId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).carritoId : null;


  //recuperar el carrito del usuario
  useEffect(() => {
    const userFromStorage = localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user"))
      : null;

    if (!userFromStorage || !userFromStorage.carritoId) {
      console.log("⚠️ No se encontró carritoId en el usuario");
      setLoading(false);
      return;
    }

    const fetchCarrito = async () => {
      try {
        setLoading(true);
        const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/${carritoId}`);
        if (!res.ok) throw new Error("No se pudo cargar el carrito");
        const data = await res.json();

        console.log("Carrito recibido del backend:", data);
        setCarrito(data);

        // Si quieres calcular el total también
        const totalRes = await fetch(`${config.apiBaseUrl}/tfg/carrito/total/${carritoId}`);
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
  }, []);

  const eliminarEvento = async (itemId) => {
    console.log("Eliminando evento con ID:", itemId);
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/itemEliminar/${usuarioId}/${itemId}`, {
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
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/finalizar/${carritoId}`, {
        method: "POST"
      });

      if (!res.ok) throw new Error("Error al finalizar la compra");
      console.log("Enviando datos de compra:", JSON.stringify(res));

      // Actualizar el carrito tras la compra
      const ticketRes = await fetch(`${config.apiBaseUrl}/tfg/ticket/findByUsuarioId/${usuarioId}`)
      const tickts = await ticketRes.json();

      setTicketsComprados(tickts)

      const total = tickts.reduce((acc, ticket) => acc + ticket.precioPagado, 0); 
      setTotalCompra(total);
      console.log("tickets comprados:", tickts);


      alert("Compra realizada con éxito!");
    } catch (err) {
      console.error(err);
      alert("No se pudo finalizar la compra");
    }
  };

  {/*if (loading) return <p className="p-4">Cargando carrito...</p>;*/ }
  if (error) return <p className="p-4 text-red-500">Error: {error}</p>;

  if (!carrito || carrito.items.length === 0)
    return (
      <div className="flex flex-col items-center justify-center min-h-screen w-full px-4">
        <div className="bg-white shadow-lg rounded-lg p-6 sm:p-8 flex flex-col items-center w-full max-w-md oscuro">
          <span className="material-symbols-outlined text-5xl sm:text-6xl text-gray-400 mb-4 osc">
            shopping_cart_off
          </span>
          <h2 className="text-xl sm:text-2xl font-bold text-gray-700 mb-2 text-center oscuroTextoGris ">Tu carrito está vacío</h2>
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
    <div className="max-w-5xl mx-auto p-6 md-6  ">
      <h1 className="text-3xl font-bold mb-6 text-blue-950 oscuroTextoGris" >Tu Carrito de Compras</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">

        {carrito.items.map((item, index) => (
          <div key={`${item.id}-${index}`} className="flex justify-between items-end-safe pr-5 gap-2 bg-white shadow border-gray-500 border-l-4 p-2 oscuro rounded">
            <div className="flex items-center gap-4">
              {/* No tienes imagen directamente, puedes poner un placeholder o hacer otra consulta */}
              <div className="flex-3">
                <h2 className="text-xl font-semibold text-blue-950 oscuroTextoGris">{item.nombreEvento}</h2>
                <p className="text-gray-500">Cantidad: {item.cantidad}</p>
                <p className="text-gray-500">Precio unitario: {item.precioUnitario.toFixed(2)} €</p>
                <p className="text-gray-500">Subtotal: {(item.precioUnitario * item.cantidad).toFixed(2)} €</p>
              </div>
            </div>
            <button
              onClick={() => eliminarEvento(item.id)}
              className="bg-red-500 text-white px-2 py-1 text-xs rounded hover:bg-red-600"
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
            className="bg-green-500 text-white p-2.5 text-sm py-2 rounded hover:bg-green-600 "
          >
            Comprar
          </button>
        </div>
        <p className="text-xl font-bold pl-2  text-blue-950 oscuroTextoGris ">Total: {total.toFixed(2)}€</p>

      </div>

      {/* Resumen tickets comprados */}
      {ticketsComprados.length > 0 && (
        <div className="mt-6 p-4 bg-white shadow rounded oscuro">

          <div className="flex my-5 w-full justify-between items-center">
            <h2 className="inline text-2xl font-semibold text-gray-500 mb-4 oscuroTextoGris">Resumen de tu compra</h2>
            <a
              href="/tickets"
              className="bg-gray-500 text-white px-4 sm:px-6 py-2 rounded-lg hover:bg-blue-600 transition w-fit text-center"
            >
              Ver mis tickets
            </a>
          </div>

          <div className="w-full flex justify-end">
            <button
              onClick={() => setReverseOrder((prev) => !prev)}
              className="mb-4 bg-gray-200 text-gray-700 px-3 py-1 rounded hover:bg-gray-300 text-xs text-right oscuroBtn"
            >
              Cambiar orden: {reverseOrder ? "Recientes" : "Antiguos"}
            </button>
          </div>

          <ul className={`flex ${reverseOrder ? "flex-col-reverse" : "flex-col"}`}>
            {ticketsComprados.map((ticket) => (
              <li key={ticket.id} className="flex md:flex-row flex-col justify-between  items-center border p-2 rounded gap-2 border-none ">
                <div className="flex items-center gap-4">
                  <span className="material-symbols-outlined text-gray-400" style={{ fontSize: "60px" }}>
                    qr_code
                  </span>

                  <div>
                    <p className="font-semibold text-blue-950 oscuroTextoGris">{ticket.nombreEvento}</p>
                    <p className="text-gray-500">Precio: {ticket.precioPagado.toFixed(2)} €</p>
                    <p className="text-gray-500">Fecha Compra: {new Date(ticket.fechaCompra).toLocaleString()}</p>
                  </div>
                </div>
                <div className="flex gap-2 md:flex-col flex-row">
                  <button
                    onClick={() => descargarPDF(ticket)}
                    className="bg-blue-500 text-white text-xs  px-3 py-1 rounded hover:bg-blue-600"
                  >
                    Descargar PDF
                  </button>
                  <button
                    onClick={() => enviarPDF(ticket)}
                    className="bg-gray-500 text-white text-xs  px-3 py-1 rounded hover:bg-gray-600"
                  >
                    Enviar PDF a mi email
                  </button>
                </div>

              </li>
            ))}
          </ul>
        </div>
      )}

    </div>
  );
}