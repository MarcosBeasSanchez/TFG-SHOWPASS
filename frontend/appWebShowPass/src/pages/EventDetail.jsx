import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import config from "../config/config";

export default function EventDetail() {
  const { id } = useParams();
  const [evento, setEvento] = useState(null);
  const [recomendaciones, setRecomendaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [animate, setAnimate] = useState(false);
  const [fullscreenImg, setFullscreenImg] = useState(null);
  const [cantidad, setCantidad] = useState(1);


  const getImageSrc = (img) => {
    if (!img) return ""; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };

  const userFromStorage = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
  // Fetch del evento
  useEffect(() => {
    const fetchEvento = async () => {
      try {
        const res = await fetch(
          `${config.apiBaseUrl}/tfg/evento/findById?id=${encodeURIComponent(id)}`
        );
        if (!res.ok) throw new Error("Evento no encontrado");
        const data = await res.json();
        console.log("Evento recibido del backend:", data);
        setEvento(data);
        setAnimate(true);
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchEvento();
  }, [id]);

  // Fetch de recomendaciones de usuario
  useEffect(() => {
    const fetchRecomendaciones = async () => {
      if (!evento?.id) return; // asegurarnos de que exista el ID
      const eventoId = evento.id;

      try {
        const res = await fetch(
          `${config.apiBaseUrl}/tfg/evento/recomendacionEvento/${eventoId}`,
          {
            method: "GET",
            headers: { "Content-Type": "application/json" },
          }
        );

        if (!res.ok) throw new Error("Error al obtener recomendaciones");

        const recomendaciones = await res.json();
        setRecomendaciones(recomendaciones);
        console.log("Recomendaciones recibidas:", recomendaciones);

      } catch (err) {
        console.error(err);
        alert("Hubo un error al obtener las recomendaciones ❌");
      }
    };
    if (userFromStorage?.id) {
      fetchRecomendaciones();
    }
  }, [evento]);

  // Formateo de fechas
  const formatDate = (fechaStr) => {
    if (!fechaStr) return "Por definir";
    const fecha = new Date(fechaStr);
    return `${fecha.getDate().toString().padStart(2, "0")}/${(fecha.getMonth() + 1).toString().padStart(2, "0")
      }/${fecha.getFullYear()} - ${fecha.getHours().toString().padStart(2, "0")}:${fecha
        .getMinutes()
        .toString()
        .padStart(2, "0")}`;
  };

  const handleEventoAlCarrito = async (cantidadSeleccionada) => {
    const user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
    const usuarioId = user?.id;
    const eventoId = evento?.id;

    if (!usuarioId || !eventoId) {
      alert("No se pudo identificar el carrito o el evento");
      return;
    }
    // Llamada al backend para agregar el evento al carrito
    try {
      const res = await fetch(
        `${config.apiBaseUrl}/tfg/carrito/item/${usuarioId}/${eventoId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ cantidad: cantidadSeleccionada }),
        }
      );

      if (!res.ok) throw new Error("Error al agregar evento al carrito");
      const data = await res.json();
      console.log("Carrito actualizado:", data);

      alert(`Entrada agregada al carrito para: ${evento.nombre} (Cantidad: ${cantidadSeleccionada})`);
    } catch (err) {
      console.error(err);
      alert("Hubo un error al agregar el evento al carrito ❌");
    }
  };



  if (loading) return <p className="p-4 text-gray-800">Cargando evento...</p>;
  if (error) return <p className="p-4 text-red-500">Error: {error}</p>;
  if (!evento) return null;

  // Fullscreen
  if (fullscreenImg) {
    return (
      <div
        className="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 transition-opacity duration-300"
        tabIndex={-1}
        onClick={() => setFullscreenImg(null)}
        onKeyDown={(e) => e.key === "Escape" && setFullscreenImg(null)}
        aria-modal="true"
        role="dialog"
      >
        <div className="relative flex flex-col items-center">
          <img
            src={getImageSrc(fullscreenImg)}
            alt="Imagen ampliada"
            className="max-h-[90vh] max-w-[90vw] shadow-2xl transition-transform duration-300 scale-100"
            onClick={(e) => e.stopPropagation()}
          />
          <button
            className="absolute top-2 right-2 h-10 w-10 text-white text-2xl font-bold bg-red-500 bg-opacity-50 rounded-full hover:bg-opacity-80 transition"
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
          src={getImageSrc(evento.imagenPrincipalUrl)}
          alt={evento.nombre}
          className="w-full h-120 object-cover"
        />
        <h1 className="absolute top-95 left-30 text-2xl sm:text-3xl md:text-4xl font-bold text-white px-4 py-2 bg-blue-950 max-w-full">
          {evento.nombre}
        </h1>
      </div>

      <div
        className={`claro oscuro shadow-xl max-w w-full p-8 
          transform transition-all duration-700 ease-out ${animate ? "opacity-100 translate-y-0" : "opacity-0 translate-y-8"}`}
        style={{ position: "relative" }}
      >
        <div className="mx-auto sm:max-w-8/8 md:max-w-7/8 lg:max-w-5/8">
          <h2 className="text-2xl font-semibold mb-0 claroEvento oscuroEvento">Datos del evento</h2>
          <div className="flex flex-col md:flex-row md:justify-between mb-0 text-gray-800 text-lg py-8">
            <div className="bg-blue-100 oscuroBox p-1">
              <strong>Localización:</strong> {evento.localizacion}
            </div>
            <div className="bg-blue-100 p-1 oscuroBox">
              <strong>Inicio:</strong> {formatDate(evento.inicioEvento)}
            </div>
            <div className="bg-blue-100 p-1 oscuroBox">
              <strong>Fin:</strong> {formatDate(evento.finEvento)}
            </div>
          </div>

          <div className="mb-6">
            <h2 className="text-2xl font-semibold mb-3 claroEvento oscuroEvento">Descripción</h2>
            <p className="text-gray-700 text-base mb-6 leading-relaxed claroEvento oscuroEvento">
              {evento.descripcion || "Este evento aún no tiene descripción."}
            </p>
            <h2 className="text-2xl font-semibold my-3 claroEvento oscuroEvento">Galería</h2>
          </div>

          {/* Carrusel de imágenes */}
          {evento.imagenesCarruselUrls && evento.imagenesCarruselUrls.length > 0 ? (
            <div className="flex flex-col items-center">
              <div className="max mx-auto">
                <div className="mb-8">
                  <div className="flex space-x-4 overflow-x-auto p-2 carrusel-sin-scrollbar">
                    {evento.imagenesCarruselUrls.map((foto, index) => (
                      <img
                        key={index}
                        src={getImageSrc(foto)}
                        alt={`Foto ${index + 1}`}
                        className="h-48 rounded-lg shadow-md object-cover flex-shrink-0 cursor-pointer transition hover:scale-105"
                        onClick={() => setFullscreenImg(foto)}
                      />
                    ))}
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <p className="mb-6 text-gray-500">No hay imágenes en la galería.</p>
          )}

          {/* Invitados */}
          <h2 className="text-2xl font-semibold mb-3 claroEvento oscuroEvento">Invitados</h2>
          {evento.invitados && evento.invitados.length > 0 ? (
            <div className="grid grid-cols-1  min-[400px]:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
              {evento.invitados.map((inv, index) => (
                <div
                  key={index}
                  className="flex flex-col items-center bg-gray-50 text-gray-800 rounded-lg shadow p-4 oscuro oscuroBox"
                >
                  {inv.fotoURL ? (
                    <img
                      src={getImageSrc(inv.fotoURL)}
                      alt={inv.nombre}
                      className="w-24 h-24 rounded-full object-cover mb-2"
                    />
                  ) : (
                    <div className="w-24 h-24 rounded-full bg-gray-300 mb-2 flex items-center justify-center">
                      <span className="text-gray-500 ">Sin foto</span>
                    </div>
                  )}
                  <h3 className="font-semibold text-center ">
                    {inv.nombre} {inv.apellidos}
                  </h3>
                  <div className="text-gray-500 text-base mt-1 text-center">{inv.descripcion}</div>
                </div>
              ))}
            </div>
          ) : (
            <div className="mb-6 text-gray-500 ">No hay invitados por ahora</div>
          )}

          <h2 className="text-2xl font-semibold mb-3 claroEvento oscuroEvento">Aforo máximo</h2>
          <p className="text-gray-700 font-bold md:text-left text-center claroEvento oscuroEvento">
            {typeof evento.aforo === "number"
              ? `${evento.aforo} personas`
              : "Aforo no disponible"}
          </p>


          {/* Precio */}
          <h2 className="text-2xl font-semibold mb-3 mt-6 claroEvento oscuroEvento">Precio</h2>
          <p className="text-gray-700 font-bold md:text-left text-center claroEvento oscuroEvento">
            {typeof evento.precio === "number"
              ? `PVP: ${evento.precio.toFixed(2)} €`
              : "Precio no disponible"}
          </p>

          {/* Recomendaciones */}
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


          {/* Botones cantidad + carrito */}
          {userFromStorage ? (
            <div className="flex md:justify-end justify-center items-center mt-3 py-8">
              <button
                type="button"
                onClick={() => setCantidad(Math.max(cantidad - 1, 1))}
                className="bg-gray-500 text-white hover:bg-red-600 transition font-bold w-10 h-10 rounded-l-lg text-2xl pb-2"
              >
                -
              </button>
              <button
                type="button"
                onClick={() => handleEventoAlCarrito(cantidad)}
                className="bg-gray-500 text-white h-10 px-5 hover:bg-green-600 transition"
                style={{ marginRight: 0 }}
              >
                <span className="material-symbols-outlined align-middle ">add_shopping_cart</span>
                Agregar al carrito: <span className="font-bold">{cantidad}</span>
              </button>
              <button
                type="button"
                onClick={() => setCantidad(cantidad + 1)}
                className="bg-gray-500 text-white hover:bg-blue-600 transition font-bold w-10 h-10 rounded-r-lg text-2xl pb-1"
              >
                +
              </button>
            </div>

          ) : (
            <p className="flex md:justify-end justify-center items-center mt-3 text-gray-700 font-bold py-8 oscuroTextoGris">
              Inicia sesión para comprar entradas 🎟️
            </p>
          )}
        </div>
      </div>
    </div>
  );
}