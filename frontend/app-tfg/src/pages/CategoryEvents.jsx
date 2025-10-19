
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import config from "../config/config";

export default function CategoriaEventos() {
  const { nombre } = useParams(); // categoría seleccionada en la URL
  const [eventos, setEventos] = useState([]);
  const [loading, setLoading] = useState(true);

  

  const getImageSrc = (img) => {
    if (!img) return "/placeholder.jpg"; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };

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
    return <p className="text-center mt-10 text-gray-500">Cargando eventos...</p>;
  }

  if (eventos.length === 0) {
    return <p className="text-center mt-10 text-gray-500">No hay eventos en la categoría <strong>{nombre}</strong>.</p>;
  }

  return (
    <div className="container mx-auto px-4 py-8 ">
      <h2 className="text-2xl font-bold mb-6 text-center text-gray-600 oscuroTextoGris ">
        {nombre}
      </h2>

      <div className="grid grid-cols-1  sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 ">
        {eventos.map((evento) => (
          <div
            key={evento.id}
            className=" text-gray-700 rounded-xl shadow-md hover:shadow-lg transition "
          >
            <img
              src={getImageSrc(evento.imagenPrincipalUrl)}
              alt={evento.nombre}
              className="w-full h-80 object-cover"
            />

            <div className="flex flex-col justify-between h-100 º p-4 fondoClaro oscuro">
              {/* Arriba */}
              <div className="">
                <h3 className="text-xl font-semibold mb-2">{evento.nombre}</h3>
                <p className="text-gray-500 mb-2 text-sm line-clamp-10 ">{evento.descripcion}</p>
              </div>
              {/* Centro */}
              <div className="flex flex-row justify-between items-center my-2">
                <div>
                  <p className="text-sm text-gray-600">
                    Inicio: {new Date(evento.inicioEvento).toLocaleString()}
                  </p>
                  <p className="text-sm text-gray-600">
                    Fin: {new Date(evento.finEvento).toLocaleString()}
                  </p>
                </div>
                <p className=" text-sm font-medium text-gray-600 bg-blue-100 oscuroBox p-1 rounded-md">
                  {Number(evento.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}€
                </p>
              </div>
              {/* Abajo */}
              <div className="text-end">
                <Link
                  to={`/evento/${evento.nombre}`}
                  className=" inline-block px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-700"
                >
                  Ver detalles
                </Link>
              </div>
            </div>

          </div>
        ))}
      </div>
    </div>
  );
};