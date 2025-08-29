
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import config from "../config/config";

export default function CategoriaEventos() {
  const { nombre } = useParams(); // categoría seleccionada en la URL
  const [eventos, setEventos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchEventos = async () => {
      try {
        const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findByCategoria/${nombre}`);
        if (!res.ok) throw new Error("Error al obtener eventos");
        const data = await res.json();
        setEventos(data);
      } catch (err) {
        console.error("❌ Error cargando eventos:", err);
        setEventos([]);
      } finally {
        setLoading(false);
      }
    };
    fetchEventos();
  }, [nombre]);

  if (loading) {
    return <p className="text-center mt-10">Cargando eventos...</p>;
  }

  if (eventos.length === 0) {
    return <p className="text-center mt-10">No hay eventos en la categoría <strong>{nombre}</strong>.</p>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h2 className="text-3xl font-bold mb-6 text-center">
        Eventos de {nombre}
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {eventos.map((evento) => (
          <div
            key={evento.id}
            className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition"
          >
            <img
              src={evento.imagen || "/placeholder.jpg"}
              alt={evento.nombre}
              className="w-full h-48 object-cover"
            />
            <div className="p-4">
              <h3 className="text-xl font-semibold mb-2">{evento.nombre}</h3>
              <p className="text-gray-600 mb-2">{evento.descripcion}</p>
              <p className="text-sm text-gray-500">
                Fecha: {new Date(evento.fechaInicio).toLocaleString()}
              </p>
              <Link
                to={`/evento/${evento.nombre}`}
                className="mt-4 inline-block px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                Ver detalle
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}