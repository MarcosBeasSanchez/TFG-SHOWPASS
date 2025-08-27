import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import config from "../config/config";

export default function EventDetail() {
  const { nombre } = useParams();
  const [evento, setEvento] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [animate, setAnimate] = useState(false);

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

  const formatDate = (fechaStr) => {
    if (!fechaStr) return "Por definir";
    const fecha = new Date(fechaStr);
    return `${fecha.getDate().toString().padStart(2, "0")}/${(fecha.getMonth() + 1).toString().padStart(2, "0")
      }/${fecha.getFullYear()} - ${fecha.getHours().toString().padStart(2, "0")}:${fecha
        .getMinutes()
        .toString()
        .padStart(2, "0")}`;
  };

  const handleComprar = () => {
    alert(`Comprar entrada para: ${evento.nombre}`);
  };

  if (loading) return <p className="p-4 text-gray-800">Cargando evento...</p>;
  if (error) return <p className="p-4 text-red-500">Error: {error}</p>;
  if (!evento) return null;

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
        <div className="max-w-4xl mx-auto text-left">
          <h2 className="text-2xl font-semibold mb-3 text-gray-800">Datos del evento</h2>
          <div className="flex flex-col md:flex-row md:justify-between mb-6 text-gray-800 text-lg py-8">
            <div><strong>Localización:</strong> {evento.localizacion}</div>
            <div><strong>Inicio:</strong> {formatDate(evento.inicioEvento)}</div>
            <div><strong>Fin:</strong> {formatDate(evento.finEvento)}</div>
          </div>

          <div className="mb-6">
            <h2 className="text-2xl font-semibold mb-3 text-gray-800">Descripción</h2>
            <p className="text-gray-700 text-base leading-relaxed">
              {evento.descripcion || "Este evento aún no tiene descripción."}
            </p>
          </div>

          {evento.carrusels && evento.carrusels.length > 0 && (
          <div className="mb-8">
            <h2 className="text-2xl font-semibold mb-3 text-gray-800">Galería</h2>
            <div className="flex space-x-4 overflow-x-auto p-2">
              {evento.carrusels.map((foto, index) => (
                <img
                  key={index}
                  src={foto}
                  alt={`Foto ${index + 1}`}
                  className="h-48 rounded-lg shadow-md object-cover flex-shrink-0"
                />
              ))}
            </div>
          </div>
          )}

        
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

          <div className="flex justify-end pr-0">
            <button
              onClick={handleComprar}
              className="bg-green-500 text-white px-6 py-3 rounded-lg hover:bg-green-600 transition"
              style={{ marginRight: 0 }}
            >
              Agregar al carrito
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}