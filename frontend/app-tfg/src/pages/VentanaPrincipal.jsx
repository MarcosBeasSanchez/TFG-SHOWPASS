import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { Link } from "react-router-dom";

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

  const mostrarBuscador = !["/login", "/register"].includes(location.pathname);

  // Separar el primer evento y el resto
  const primerEvento = entradasFiltradas[0]; //primer evento para un div de lado a lado
  const restoEventos = entradasFiltradas.slice(1);

  return (
    <div>
      {mostrarBuscador && (
        <div className="flex justify-center items-center mb-6 bg-gray-800 p-4">
          <div className="flex items-center gap-4 w-full max-w-3xl bg-gray-700 rounded-md px-2">
            <span className="material-symbols-outlined text-2xl">search</span>
            <input
              type="text"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className=" p-2 border-gray-800 w-100"
              placeholder="Escribe el nombre del evento..."
            />
          </div>
        </div>
      )}

      {primerEvento && (
        <div className="flex justify-center p-10 w-full">
          <div
            key={primerEvento.id}
            className="bg-white shadow-lg overflow-hidden hover:scale-101 transition transform w-full group"
          >
            <Link to={`/evento/${primerEvento.nombre}`}>
              <div className="relative w-full h-80">
                <img
                  src={primerEvento.imagen}
                  alt={primerEvento.nombre}
                  className="w-full h-80 object-cover transition duration-500 group-hover:opacity-70"
                />
                <div className="absolute inset-0 bg-blue-500 opacity-0 group-hover:opacity-20 transition duration-500"></div>
              </div>
            </Link>
            <div className="p-4 flex flex-col justify-between h-56">
              <div>
                <h3 className="text-xl font-bold text-gray-700">{primerEvento.nombre}</h3>
                <p className="text-gray-600 mt-2">{primerEvento.localizacion}</p>
                <p className="text-gray-500 mt-2">
                  {primerEvento.inicioEvento
                    ? new Date(primerEvento.inicioEvento).toLocaleString()
                    : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-2">
                  {primerEvento.invitados?.length
                    ? `Invitados: ${primerEvento.invitados.map(i => i.nombre).join(", ")}`
                    : "Sin invitados por ahora"}
                </p>
              </div>
              <Link
                to={`/evento/${primerEvento.nombre}`}
                className="mt-4 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center"
              >
                Ver detalles
              </Link>
            </div>
          </div>
        </div>
      )}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-10 p-10">
        {restoEventos.map((entrada) => (
          <div
            key={entrada.id}
            className="bg-white shadow-lg overflow-hidden hover:scale-101 transition transform group"
          >
            <Link to={`/evento/${entrada.nombre}`}>
              <div className="relative w-full h-80">
                <img
                  src={entrada.imagen}
                  alt={entrada.nombre}
                  className="w-full h-80 object-cover transition duration-500 group-hover:opacity-70"
                />
                <div className="absolute inset-0 bg-blue-500 opacity-0 group-hover:opacity-20 transition duration-500"></div>
              </div>
            </Link>
            <div className="p-4 flex flex-col justify-between h-56">
              <div>
                <h3 className="text-xl font-bold text-gray-700">{entrada.nombre}</h3>
                <p className="text-gray-600 mt-2">{entrada.localizacion}</p>
                <p className="text-gray-500 mt-2">
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
              <Link
                to={`/evento/${entrada.nombre}`}
                className="mt-4 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center"
              >
                Ver detalles
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}