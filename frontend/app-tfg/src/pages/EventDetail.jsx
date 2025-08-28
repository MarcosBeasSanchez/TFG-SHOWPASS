import { use, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import config from "../config/config";

export default function EventDetail() {
  const { nombre } = useParams();
  const [evento, setEvento] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [animate, setAnimate] = useState(false);
  // Estado para la imagen en pantalla completa
  const [fullscreenImg, setFullscreenImg] = useState(null);
  // Estado para la cantidad tickets, empieza siempre en 1
  const [cantidad, setCantidad] = useState(1);

  /*
  // Estado para almacenar datos de compra de ticket
  const [ticketCompra, setTicketCompra] = useState({
    usuarioId: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null,
    eventoId: evento ? evento.id : null,
    codigoQR: null,
    fechaCompra: null
  });

  const enviarTicketCompra = async () => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/ticket/insert`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(ticketCompra)
      });
      if (!res.ok) throw new Error("Error al comprar el ticket");
      const data = await res.json();
      alert("Ticket comprado correctamente");
    } catch (err) {
      setError(err.message);
    }
  };
  
  */

  {/*ENDPOINT Evento*/ }
  useEffect(() => {
    const fetchEvento = async () => {
      try {
        const res = await fetch(
          `${config.apiBaseUrl}/tfg/evento/findByNombre?nombre=${encodeURIComponent(nombre)}`
        );
        if (!res.ok) throw new Error("Evento no encontrado");
        const data = await res.json();
        setEvento(data);
        setAnimate(true); // disparar animación al recibir datos
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchEvento();

  }, [nombre]);

  {/*Formatear fecha */ }
  const formatDate = (fechaStr) => {
    if (!fechaStr) return "Por definir";
    const fecha = new Date(fechaStr);
    return `${fecha.getDate().toString().padStart(2, "0")}/${(fecha.getMonth() + 1).toString().padStart(2, "0")
      }/${fecha.getFullYear()} - ${fecha.getHours().toString().padStart(2, "0")}:${fecha
        .getMinutes()
        .toString()
        .padStart(2, "0")}`;
  };

  {/*Evento al carrito */ }
  const handleEventoAlCarrito = async (cantidadSeleccionada) => {
    const usuarioId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;
    const eventoId = evento ? evento.id : null;

    try {
      for (let i = 0; i < cantidadSeleccionada; i++) {
        const res = await fetch(
          `http://localhost:8080/tfg/carrito/agregar/${usuarioId}/${eventoId}`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ cantidad: 1 })
          }
        );

        if (!res.ok) {
          throw new Error("Error al agregar evento al carrito");
        }

        const data = await res.json();
        console.log(`Carrito actualizado (${i + 1}/${cantidadSeleccionada}):`, data);
      }
      alert(`Entrada agregada al carrito para: ${evento.nombre} (Cantidad: ${cantidadSeleccionada})`);
    } catch (err) {
      console.error(err);
      alert("Hubo un error al agregar el evento al carrito ❌");
      console.error("usuarioId:", usuarioId, " eventoId: ", eventoId);
    }
  };

  if (loading) return <p className="p-4 text-gray-800">Cargando evento...</p>;
  if (error) return <p className="p-4 text-red-500">Error: {error}</p>;
  if (!evento) return null;

  {/*Fullscreen  */ }
  if (fullscreenImg) {
    return (
      <div
        className="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 transition-opacity duration-300"
        tabIndex={-1}
        onClick={() => setFullscreenImg(null)}
        onKeyDown={e => {
          if (e.key === "Escape") setFullscreenImg(null);
        }}
        aria-modal="true"
        role="dialog"
      >
        <div className="relative flex flex-col items-center">
          <img
            src={fullscreenImg}
            alt="Imagen ampliada"
            className="max-h-[90vh] max-w-[90vw]  shadow-2xl transition-transform duration-300 scale-100"
            onClick={e => e.stopPropagation()}
          />
          <button
            className="absolute top-2 right-2 h-10 w-10 text-white  text-2xl font-bold bg-red-500 bg-opacity-50 rounded-full    hover:bg-opacity-80 transition"
            style={{ zIndex: 60 }}
            onClick={() => setFullscreenImg(null)}
            aria-label="Cerrar"
          >
            ×
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center">
      <div className="relative w-full">
        <img
          src={evento.imagen}
          alt={evento.nombre}
          className="w-full h-120 object-cover"
        />
        <h1 className="absolute top-95 left-30 text-4xl font-bold text-white px-4 py-2 bg-blue-950">
          {evento.nombre}
        </h1>
      </div>
      <div
        className={`bg-white shadow-xl max-w w-full p-8 transform transition-all duration-700 ease-out
        ${animate ? "opacity-100 translate-y-0" : "opacity-0 translate-y-8"}`}
        style={{ position: "relative" }}
      >
        {/* Datos del evento */}
        <div className="max-w-4xl mx-auto text-left">
          <h2 className="text-2xl font-semibold mb-3 text-gray-800">Datos del evento</h2>
          <div className="flex flex-col md:flex-row md:justify-between mb-6 text-gray-800 text-lg py-8">
            <div className="bg-blue-100 p-1"><strong>Localización:</strong> {evento.localizacion}</div>
            <div className="bg-blue-100 p-1"><strong>Inicio:</strong> {formatDate(evento.inicioEvento)}</div>
            <div className="bg-blue-100 p-1"><strong>Fin:</strong> {formatDate(evento.finEvento)}</div>
          </div>

          {/* Descripción */}
          <div className="mb-6">
            <h2 className="text-2xl font-semibold mb-3 text-gray-800">Descripción</h2>
            <p className="text-gray-700 text-base leading-relaxed">
              {evento.descripcion || "Este evento aún no tiene descripción."}
            </p>
            <h2 className="text-2xl font-semibold my-3 text-gray-800">Galería</h2>
          </div>

          {/* Carrusel de imágenes */}
          <div className="flex flex-col items-center">

            <div className="max-w-4xl mx-auto text-left">
              {evento.carrusels && evento.carrusels.length > 0 && (
                <div className="mb-8">
                  <div className="flex space-x-4 overflow-x-auto p-2">
                    {evento.carrusels.map((foto, index) => (
                      <img
                        key={index}
                        src={foto}
                        alt={`Foto ${index + 1}`}
                        className="h-48 rounded-lg shadow-md object-cover flex-shrink-0 cursor-pointer transition hover:scale-105"
                        onClick={() => setFullscreenImg(foto)}
                      />
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Invitados */}
          <h2 className="text-2xl font-semibold mb-3 text-gray-800">Invitados</h2>
          {evento.invitados && evento.invitados.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
              {evento.invitados.map((inv, index) => (
                <div
                  key={index}
                  className="bg-gray-50 rounded-lg shadow p-4 flex flex-col items-center text-gray-800"
                >
                  {inv.fotoURL ? (
                    <img
                      src={inv.fotoURL}
                      alt={inv.nombre}
                      className="w-24 h-24 rounded-full object-cover mb-2"
                    />
                  ) : (
                    <div className="w-24 h-24 rounded-full bg-gray-300 mb-2 flex items-center justify-center">
                      <span className="text-gray-500">Sin foto</span>
                    </div>
                  )}
                  <h3 className="font-semibold text-center">{inv.nombre} {inv.apellidos}</h3>
                  <div className="text-gray-700 text-base mt-1 text-center">{inv.descripcion}</div>
                </div>
              ))}
            </div>
          ) : (
            <div className="mb-6 text-gray-800 text-base md:text-lg">No hay invitados por ahora</div>
          )}

          {/* Precio */}
          <h2 className="text-2xl font-semibold mb-3 text-gray-800">Precio</h2>
          <p className="text-gray-700 font-bold md:text-left text-center">
            {typeof evento.precio === "number"
              ? `PVP: ${evento.precio.toFixed(2)} €`
              : "Precio no disponible"}
          </p>



          {/* agregar al carrito */}
          <div className="flex  md:justify-end  justify-center items-center  mt-3">
            <button
              type="button"
              onClick={() => setCantidad(Math.max(cantidad - 1, 1))}
              className="bg-gray-500 text-white hover:bg-red-600 transition font-bold w-10 h-10 rounded-l-full "
            >
              -
            </button>
            <button
              type="button"
              onClick={() => handleEventoAlCarrito(cantidad)}
              className="bg-gray-500 text-white h-10 px-5 hover:bg-green-600 transition "
              style={{ marginRight: 0 }}
            >
              Agregar al carrito: <span className="font-bold">{cantidad}</span>
              <span className="material-symbols-outlined align-middle pl-2">add_shopping_cart</span>
            </button>
            <button
              type="button"
              onClick={() => setCantidad(cantidad + 1)}
              className="bg-gray-500 text-white  hover:bg-blue-600 transition font-bold w-10 h-10 rounded-r-full"
            >
              +
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}