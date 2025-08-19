import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

export default function VentanaPrincipal() {
  const [entradas, setEntradas] = useState([]);
  const [busqueda, setBusqueda] = useState("");
  const location = useLocation();

  useEffect(() => {
    const fetchEventos = async () => {
      try {
        const res = await fetch("http://localhost:8080/tfg/evento/findAll");
        const data = await res.json();
        setEntradas(data);
      } catch (err) {
        console.error("Error cargando eventos:", err);
      }
    };

    fetchEventos();
  }, []);

  const entradasFiltradas = entradas.filter((e) =>
    e.nombre.toLowerCase().includes(busqueda.toLowerCase())
  );

  // No mostrar el buscador en login o register
  const mostrarBuscador = !["/login", "/register"].includes(location.pathname);

  return (
    <div className="p-8">
      {mostrarBuscador && (
        <div className="flex justify-center mb-6">
          <input
            type="text"
            placeholder="Buscar evento..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            className="w-full max-w-md p-2 border rounded-lg focus:outline-none focus:ring focus:border-blue-300"
          />
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {entradasFiltradas.map((entrada) => (
          <div
            key={entrada.id}
            className="bg-white rounded-2xl shadow-lg overflow-hidden hover:scale-105 transition transform"
          >
            <img
              src={entrada.imagen}
              alt={entrada.nombre}
              className="w-full h-48 object-cover"
            />
            <div className="p-4 flex flex-col justify-between h-56">
              <div>
                <h3 className="text-xl font-bold text-gray-700">{entrada.nombre}</h3>
                <p className="text-gray-600 mt-2">
                  {entrada.localizacion} |{" "}
                  {entrada.inicioEvento
                    ? new Date(entrada.inicioEvento).toLocaleString()
                    : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-2">
                  {entrada.invitados?.length
                    ? `Invitados: ${entrada.invitados.map(i => i.nombre).join(", ")}`
                    : "Sin invitados por ahora"}
                </p>
              </div>
              <button
                className="mt-4 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600"
                onClick={() => alert(`Aquí iría la compra de: ${entrada.nombre}`)}
              >
                Comprar
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}