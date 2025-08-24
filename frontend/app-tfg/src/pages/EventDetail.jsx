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
    return `${fecha.getDate().toString().padStart(2, "0")}/${
      (fecha.getMonth() + 1).toString().padStart(2, "0")
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
    <div className="flex justify-center p-8 bg-gray-100 min-h-screen">
      <div
        className={`bg-white rounded-xl shadow-xl max-w-5xl w-full p-8 transform transition-all duration-700 ease-out
        ${animate ? "opacity-100 translate-y-0" : "opacity-0 translate-y-8"}`}
      >
        <h1 className="text-3xl font-bold mb-4 text-gray-800">{evento.nombre}</h1>
        <img
          src={evento.imagen}
          alt={evento.nombre}
          className="w-full h-96 object-cover rounded-lg mb-6"
        />
        <div className="mb-6 text-gray-800">
          <p><strong>Localización:</strong> {evento.localizacion}</p>
          <p><strong>Inicio:</strong> {formatDate(evento.inicioEvento)}</p>
          <p><strong>Fin:</strong> {formatDate(evento.finEvento)}</p>
        </div>

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
                <p className="text-gray-700 text-sm mt-1 text-center">{inv.descripcion}</p>
              </div>
            ))}
          </div>
        ) : (
          <p className="mb-6 text-gray-800">No hay invitados por ahora</p>
        )}

        <button
          onClick={handleComprar}
          className="bg-green-500 text-white px-6 py-3 rounded-lg hover:bg-green-600 transition"
        >
          Comprar entrada
        </button>
      </div>
    </div>
  );
}