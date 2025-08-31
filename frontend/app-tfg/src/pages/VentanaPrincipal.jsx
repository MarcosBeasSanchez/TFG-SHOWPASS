import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

export default function VentanaPrincipal() {
  const [entradas, setEntradas] = useState([]);
  const [entradasAleatorias, setEntradasAleatorias] = useState([]);
  const [busqueda, setBusqueda] = useState("");
  const location = useLocation();
  const navigate = useNavigate();

  const mostrarBuscador = !["/login", "/register"].includes(location.pathname);

  // Redirigir a la página de búsqueda al pulsar Enter
  const handleInputKeyDown = (e) => {
    if (e.key === "Enter" && busqueda.trim()) {
      navigate(`/busqueda?query=${encodeURIComponent(busqueda.trim())}`);
    }
  };

  // Redirigir al hacer click en el icono de búsqueda
  const handleSearchClick = () => {
    if (busqueda.trim()) {
      navigate(`/busqueda?query=${encodeURIComponent(busqueda.trim())}`);
    }
  };

  useEffect(() => {
    const fetchEventos = async () => {
      try {
        const res = await fetch("http://localhost:8080/tfg/evento/findAll");
        const data = await res.json();
        setEntradas(data);
        // Mezclar solo una vez al cargar
        setEntradasAleatorias([...data].sort(() => Math.random() - 0.5));
      } catch (err) {
        console.error("Error cargando eventos:", err);
      }
    };

    fetchEventos();
  }, []);

  const primerEvento = entradasAleatorias[0];
  const restoEventos = entradasAleatorias.slice(1);

  return (
    <div>
      {mostrarBuscador && (
        <div className="flex justify-center items-center bg-gray-800 p-4">
          <div className="flex items-center gap-4 w-full max-w-3xl bg-gray-700 rounded-md px-2">
            <input
              type="text"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              onKeyDown={handleInputKeyDown}
              className="p-2 border-gray-800 w-full"
              placeholder="Escribe el nombre del evento..."
              maxLength={20}
            />
            <span
              className="material-symbols-outlined text-2xl cursor-pointer transition-transform duration-200 hover:scale-125 hover:text-blue-400"
              onClick={handleSearchClick}
            >
              search
            </span>
          </div>
        </div>
      )}

      {primerEvento && (
        <div className="grid place-items-center grid-cols-1 p-5 sm:p-10">
          <h1 className="text-2xl font-bold mb-4 text-gray-600">PRÓXIMOS EVENTOS</h1>
          <div
            key={primerEvento.id}
            className="bg-white shadow-lg overflow-hidden hover:scale-101 transition transform lg:w-4/6 md:w-5/6 sm:w-full group"
          >
            <Link to={`/evento/${primerEvento.nombre}`}>
              <div className="relative w-full ">
                <img
                  src={primerEvento.imagen}
                  alt={primerEvento.nombre}
                  className="w-full h-100 object-cover transition duration-500 group-hover:opacity-70"
                />
                <div className="absolute inset-0 bg-blue-500 opacity-0 group-hover:opacity-20 transition duration-500"></div>
              </div>
            </Link>
            <div className="p-4 flex flex-col justify-between ">
              <h3 className="text-xl font-semibold text-gray-700">{primerEvento.nombre}</h3>
              <div className="my-2">
                <p className="text-gray-500 mt-1">
                  <strong>Localización:</strong> {primerEvento.localizacion}</p>
                <p className="text-gray-500 mt-1">
                  <strong>Inicio Evento:</strong> {primerEvento.inicioEvento
                    ? new Date(primerEvento.inicioEvento).toLocaleString()
                    : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-1">
                  <strong>Final Evento:</strong> {primerEvento.finEvento
                    ? new Date(primerEvento.finEvento).toLocaleString()
                    : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-1">
                  {primerEvento.invitados?.length
                    ? `Invitados: ${primerEvento.invitados.map(i => i.nombre).join(", ")}`
                    : "Sin invitados por ahora"}
                </p>
              </div>
              <div className="flex justify-end gap-2 items-baseline">
                <p className="font-medium text-sm text-gray-600 bg-blue-100 p-2 rounded-md">
                  {Number(primerEvento.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}€
                </p>
                <Link
                  to={`/evento/${primerEvento.nombre}`}
                  className=" bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center"
                >
                  Ver detalles
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Encabezado principal */}
      <div className="flex-row ">
        <h1
          className="flex items-center justify-center text-2xl 
          sm:text-4xl 
          md:text-5xl 
          lg:text-6xl 
          font-extrabold text-center py-7 bg-blue-900 text-white opacity-20 gap-4"
          style={{ lineHeight: "1", fontFamily: "Roboto, sans-serif", minWidth: "320px" }}
        >
          SHOWPASS
          <span
            className="material-symbols-outlined"
            style={{ fontSize: "1em", lineHeight: "1" }}
          >
            local_activity
          </span>
        </h1>
      </div>

      <div className="grid  md:grid-cols-2 lg:grid-cols-3 gap-10  p-5 sm:p-10">
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
            <div className="p-4 flex flex-col h-auto">
              <h3 className="text-xl font-semibold text-gray-700">{entrada.nombre}</h3>
              <div className="my-2">
                <p className="text-gray-500 mt-1"><strong>Localización:</strong> {entrada.localizacion}</p>
                <p className="text-gray-500 mt-1">
                  <strong>Inicio Evento:</strong> {entrada.inicioEvento
                    ? new Date(entrada.inicioEvento).toLocaleString()
                    : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-1"><strong>Final Evento:</strong> {entrada.finEvento
                  ? new Date(entrada.finEvento).toLocaleString()
                  : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-1">
                  {entrada.invitados?.length
                    ? `Invitados: ${entrada.invitados.map(i => i.nombre).join(", ")}`
                    : "Sin invitados por ahora"}
                </p>
              </div>
              <div className="flex justify-end items-baseline mt-1 gap-2">
                <p className=" text-sm font-medium text-gray-600 bg-blue-100 p-2 rounded-md">
                  {Number(entrada.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}€
                </p>
                <Link
                  to={`/evento/${entrada.nombre}`}
                  className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center inline-block w-auto"
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
}