import { useEffect, useState } from "react";
import config from "../config/config";
import { descargarPDF, enviarPDF } from "../utils/entradasPdf";

/**
 * Componente que gestiona el carrito de compras y la finalización de la compra del usuario.
 * Muestra los ítems en el carrito, el total, permite modificarlo y muestra las recomendaciones.
 */
export default function ShoppingCart() {

  // ----------------------------------------------------
  // 1. ESTADO
  // ----------------------------------------------------
  // Estado para almacenar la estructura del carrito (ej: {id, items: [...]}).
  const [carrito, setCarrito] = useState(null);
  // Estado para almacenar el precio total de los ítems en el carrito.
  const [total, setTotal] = useState(0);
  // Estado para almacenar los eventos recomendados por la IA.
  const [recomendaciones, setRecomendaciones] = useState([]);
  // Estado para gestionar el estado de carga (loading).
  const [loading, setLoading] = useState(true);
  // Estado para gestionar y mostrar errores de fetching.
  const [error, setError] = useState(null);
  // Estado para almacenar la lista de tickets comprados tras finalizar la compra.
  const [ticketsComprados, setTicketsComprados] = useState([]);
  // Estado para almacenar el total gastado en la última compra.
  const [totalCompra, setTotalCompra] = useState([]);
  // Estado para controlar el orden de visualización de los tickets comprados (Recientes/Antiguos).
  const [reverseOrder, setReverseOrder] = useState(true); // NUEVA VARIABLE

  // ----------------------------------------------------
  // 2. RECUPERACIÓN DE DATOS DEL USUARIO (localStorage)
  // ----------------------------------------------------

  // Extrae el ID del usuario.
  const usuarioId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;
  // Extrae el ID del carrito asociado al usuario.
  const carritoId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).carritoId : null;
  // Obtiene la información del usuario desde localStorage.
  const userFromStorage = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;

  // ----------------------------------------------------
  // 3. EFECTOS: Carga Inicial del Carrito
  // ----------------------------------------------------

  // useEffect se ejecuta al montar el componente para cargar el carrito.
  useEffect(() => {
    const userFromStorage = localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user"))
      : null;

    // Comprueba si el usuario y el ID del carrito existen.
    if (!userFromStorage || !userFromStorage.carritoId) {
      console.log("⚠️ No se encontró carritoId en el usuario");
      setLoading(false);
      return;
    }

    const fetchCarrito = async () => {
      try {
        setLoading(true);
        // 1. Fetch del contenido del carrito
        const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/${carritoId}`);
        if (!res.ok) throw new Error("No se pudo cargar el carrito");
        const data = await res.json();

        console.log("Carrito recibido del backend:", data);
        setCarrito(data);

        // 2. Fetch del total € del carrito
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
    fetchCarrito(); // Llama a la función para cargar el carrito.
  }, []); // El array vacío [] asegura que se ejecute solo una vez al montar el componente.

  // ----------------------------------------------------
  // 4. FUNCIONES DE MANEJO DEL CARRITO (CRUD)
  // ----------------------------------------------------
  /**
   * Elimina un ítem específico del carrito.
   * @param {number} itemId - ID del ítem del carrito (CarritoItem) a eliminar.
   * */
  const eliminarEvento = async (itemId) => {
    console.log("Eliminando evento con ID:", itemId);
    try {
      // Petición DELETE para eliminar un ítem del carrito.
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/itemEliminar/${usuarioId}/${itemId}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error("Error eliminando evento");
      // Recibe el carrito actualizado como respuesta.

      const data = await res.json();
      setCarrito(data); // Actualiza el estado del carrito

      // Actualizar total
      const totalRes = await fetch(`${config.apiBaseUrl}/tfg/carrito/total/${usuarioId}`);
      const totalData = await totalRes.json();
      setTotal(totalData);
    } catch (err) {
      console.error(err);
      alert("No se pudo eliminar el evento");
    }
  };

  /**
  * Vacía completamente el carrito del usuario.
  */
  const vaciarCarrito = async () => {
    try {
      // Petición DELETE para vaciar el carrito.
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

  /**
  * Finaliza la compra, convirtiendo el contenido del carrito en tickets.
  */
  const finalizarCompra = async () => {
    try {
      // Petición POST para finalizar la compra.
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/finalizar/${carritoId}`, {
        method: "POST"
      });

      if (!res.ok) throw new Error("Error al finalizar la compra");
      console.log("Enviando datos de compra:", JSON.stringify(res));

      // 1. Obtener los tickets recién comprados
      const ticketRes = await fetch(`${config.apiBaseUrl}/tfg/ticket/findByUsuarioId/${usuarioId}`)
      const tickts = await ticketRes.json();

      setTicketsComprados(tickts) // Actualiza el estado con los tickets comprados

      // 2. Calcular y almacenar el total de la compra
      const total = tickts.reduce((acc, ticket) => acc + ticket.precioPagado, 0);
      setTotalCompra(total);
      console.log("tickets comprados:", tickts);


      alert("Compra realizada con éxito!");
    } catch (err) {
      console.error(err);
      alert("No se pudo finalizar la compra");
    }
  };


  // ----------------------------------------------------
  // 5. EFECTO: Carga de Recomendaciones (Basadas en IA/Usuario)
  // ----------------------------------------------------

  // Este efecto se ejecuta al montar y si cambia el ID del usuario (aunque solo lo hace al inicio).
  useEffect(() => {
    const fetchRecomendaciones = async () => {
      try {
        // Petición GET a la API para obtener recomendaciones personalizadas.
        const res = await fetch(
          `${config.apiBaseUrl}/tfg/evento/recomendacionUsuario/${userFromStorage?.id}`,
          {
            method: "GET",
            headers: { "Content-Type": "application/json" },
          }
        );

        if (!res.ok) throw new Error("Error al obtener recomendaciones");

        const recomendaciones = await res.json();
        setRecomendaciones(recomendaciones); // Actualiza el estado con las recomendaciones recibidas.
        console.log("Recomendaciones recibidas:", recomendaciones);

      } catch (err) {
        console.error(err);
        alert("Hubo un error al obtener las recomendaciones ❌");
      }
    };
    if (userFromStorage?.id) {
      fetchRecomendaciones();
    }
  }, [userFromStorage?.id]);


  // ----------------------------------------------------
  // 6. RENDERIZADO CONDICIONAL (Loading, Error, Carrito Vacío)
  // ----------------------------------------------------

  {/*if (loading) return <p className="p-4">Cargando carrito...</p>;*/ }
  if (error) return <p className="p-4 text-red-500">Error: {error}</p>;

  // Caso de carrito vacío o no encontrado
  if (!carrito || carrito.items.length === 0)
    return (
      <div className="flex flex-col items-center justify-center min-h-screen w-full px-4">
        {/* JSX para mostrar el mensaje de carrito vacío */}
        <div className="bg-white shadow-lg rounded-lg p-6 sm:p-8 flex flex-col items-center w-full max-w-md oscuro">
          <span className="material-symbols-outlined text-5xl sm:text-6xl text-gray-400 mb-4 osc">
            shopping_cart_off
          </span>
          <h2 className="text-xl sm:text-2xl font-bold text-gray-700 mb-2 text-center oscuroTextoGris ">Tu carrito está vacío</h2>
          <p className="text-gray-500 mb-4 text-center">¡Añade eventos para empezar tu compra!</p>
          {/* GIF de un carrito vacío */}
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
  // ----------------------------------------------------
  // 7. RENDERIZADO PRINCIPAL (Carrito con contenido)
  // ----

  return (
    <div className="max-w-5xl mx-auto p-6 md-6  ">
      <h1 className="text-3xl font-bold mb-6 text-blue-950 oscuroTextoGris" >Tu Carrito de Compras</h1>

      {/* Listado de ítems del carrito */}
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

      {/* Controles de carrito */}
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

      {/* Recomendaciones de Eventos */}
      <h2 className="text-2xl font-semibold mb-3 mt-6 claroEvento oscuroEvento">
        Eventos recomendados con IA
      </h2>
      {recomendaciones.length > 0 ? (
        <div className="overflow-x-auto flex space-x-4 pb-4 carrusel-sin-scrollbar">
          {recomendaciones.map((rec) => (
            <div
              key={rec.id}
              className="bg-gray-50 rounded-lg shadow flex-none flex flex-col text-gray-800 oscuroBox w-50 cursor-pointer"
              onClick={() => window.location.href = `/evento/${encodeURIComponent(rec.id)}`}
            >
              {/* Renderizado de imagen o placeholder */}
              {rec.imagen ? (
                <>
                  <img
                    src={rec.imagen}
                    alt={rec.nombre}
                    className="w-full h-50 object-cover aspect-square rounded-t-lg"
                  />
                  <div className="flex flex-col items-center justify-center p-4 text-center w-full">
                    <h3 className="text-lg font-medium">{rec.nombre}</h3>
                    <p className="text-sm">{rec.localizacion}</p>
                    <p className="text-sm font-semibold mt-1">{rec.precio} €</p>
                  </div>
                </>
              ) : (
                <div className="w-50 h-50 rounded-lg bg-gray-300 flex items-center justify-center">
                  <span className="text-gray-500">Sin imagen</span>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="mb-6 text-gray-500">No hay recomendaciones por ahora</div>
      )}


      {/*Resumen tickets comprados (Se muestra después de finalizar la compra) */}
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

          {/* Botón para alternar el orden de los tickets */}
          <div className="w-full flex justify-end">
            <button
              onClick={() => setReverseOrder((prev) => !prev)}
              className="mb-4 bg-gray-200 text-gray-700 px-3 py-1 rounded hover:bg-gray-300 text-xs text-right oscuroBtn"
            >
              Cambiar orden: {reverseOrder ? "Recientes" : "Antiguos"}
            </button>
          </div>

          {/* Lista de tickets comprados (el orden depende de 'reverseOrder') */}
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