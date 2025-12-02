import { useEffect, useState } from "react";
import config from "../config/config";
import { useLocation, useNavigate, Link } from "react-router-dom";


//---------------
// Componente Principal: VentanaPrincipal
//-------------

/**
 * Componente principal de la página de inicio que muestra el buscador
 * y una lista aleatoria de eventos disponibles.
 */
export default function VentanaPrincipal() {
  // Estado para almacenar todos los eventos (entradas) cargados.
  const [entradas, setEntradas] = useState([]);
  // Estado para almacenar los eventos mezclados (aleatorios) para la visualización.
  const [entradasAleatorias, setEntradasAleatorias] = useState([]);
  // Estado para el texto introducido en la barra de búsqueda.
  const [busqueda, setBusqueda] = useState("");
  // Hook para acceder al objeto de localización de React Router (info de la URL actual).
  const location = useLocation();
  // Hook para la navegación programática.
  const navigate = useNavigate();
  // Estado para controlar el estado de carga de los eventos.
  const [loading, setLoading] = useState(true);
  // Determina si se debe mostrar el buscador (no se muestra en /login o /register).
  const mostrarBuscador = !["/login", "/register"].includes(location.pathname);


//---------------
// Seccion: Funciones de Búsqueda y Navegación
//-------------
  /**
   * Redirige a la página de resultados de búsqueda al presionar la tecla "Enter"
   * si el campo de búsqueda no está vacío.
   * @param {Event} e - Evento de teclado.
   */
  const handleInputKeyDown = (e) => {
    if (e.key === "Enter" && busqueda.trim()) {
      navigate(`/busqueda?query=${encodeURIComponent(busqueda.trim())}`);
    }
  };

 /**
   * Redirige a la página de resultados de búsqueda al hacer clic en el icono de búsqueda.
   */
  const handleSearchClick = () => {
    if (busqueda.trim()) {
      // Redirigir a la página de búsqueda con el término de búsqueda
      navigate(`/busqueda?query=${encodeURIComponent(busqueda.trim())}`);
    }
  };


//---------------
// Seccion: Funciones de Utilidad (Imágenes)
//-------------

  /**
   * Devuelve la fuente (src) correcta para mostrar una imagen.
   * Maneja Base64, URLs externas y rutas relativas del backend.
   * @param {string} img - La URL o cadena Base64 de la imagen.
   * @returns {string|null} - La fuente de la imagen.
   */
  const getImageSrc = (img) => {
    if (!img) return null; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };
  
//---------------
// Seccion: useEffect - Carga de Eventos
//-------------

  // Cargar eventos desde el backend al montar el componente.
  useEffect(() => {
    const fetchEventos = async () => {
      try {
        const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findAll`);
        const data = await res.json();
        setEntradas(data);
        console.log("Eventos recibidos del backend:", data);
        // Mezclar los eventos aleatoriamente solo una vez
        setEntradasAleatorias([...data].sort(() => Math.random() - 0.5));
      } catch (err) {
        console.error("Error cargando eventos:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchEventos();// Llamada a la función para cargar los eventos.
  }, []); // El array vacío asegura que esto solo se ejecute una vez al montar el componente.

//---------------
// Seccion: Renderizado del Componente
//-------------
  // Separar el primer evento del resto para destacarlo.
  const primerEvento = entradasAleatorias[0];
  // El resto de eventos
  const restoEventos = entradasAleatorias.slice(1);

  
  // Obtener usuario desde localStorage para depuración
  const userFromStorage = localStorage.getItem("user")? JSON.parse(localStorage.getItem("user")): null;
  console.log("Usuario desde localStorage:", userFromStorage);

  // Muestra un mensaje de carga mientras se obtienen los datos.
  if (loading) {
    return <p className="text-center mt-10 text-gray-500">Cargando eventos...</p>;
  }

//---------------
// Seccion: Renderizado del Componente
//-------------
  return (
    <div>
      {/* Barra de Búsqueda Condicional */}
      {mostrarBuscador && (
        <div className="flex justify-center items-center bg-gray-800 p-4">
          <div className="flex items-center gap-4 w-full max-w-3xl bg-gray-700 rounded-md px-2">
            <input
              type="text"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              onKeyDown={handleInputKeyDown}
              className="p-2 border-gray-800 w-full"
              placeholder="Buscar eventos por nombre, localización, descripción o categoría..."
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

      {/* Contenedor Principal de Eventos */}
      <div className="max-w-7/8 mx-auto ">

      {/* Renderizado del Evento Destacado (el primero de la lista aleatoria) */}
        {primerEvento && (
          <div className="grid place-items-center p-5 sm:p-10 ">
            <h1 className="text-2xl font-bold mb-4 text-gray-600 oscuroTextoGris">PRÓXIMOS EVENTOS</h1>
            <div
              key={primerEvento.id}
              className="bg-white shadow-lg overflow-hidden hover:scale-101 transition transform lg:w-full w-full group"
            >
              <Link to={`/evento/${primerEvento.id}`}>
                <div className="relative w-full ">
                  {/* Usamos getImageSrc para asegurar que la imagen sea válida */}
                  <img
                    src={getImageSrc(primerEvento.imagenPrincipalUrl)}
                    alt={primerEvento.nombre}
                    className="w-full h-100 object-cover transition duration-500 group-hover:opacity-70"
                  />
                  <div className="absolute inset-0 bg-blue-500 opacity-0 group-hover:opacity-20 transition duration-500"></div>
                </div>
              </Link>

              <div className="p-4 flex flex-col bg-white oscuro">
                <h3 className="text-xl font-semibold text-gray-700 oscuro">{primerEvento.nombre}</h3>
                <div className="my-2">
                  <p className="text-gray-500 mt-1"><strong>Localización:</strong> {primerEvento.localizacion}</p>
                  <p className="text-gray-500 mt-1">
                    <strong>Inicio Evento:</strong> {primerEvento.inicioEvento ? new Date(primerEvento.inicioEvento).toLocaleString() : "Fecha por definir"}
                  </p>
                  <p className="text-gray-500 mt-1">
                    <strong>Final Evento:</strong> {primerEvento.finEvento ? new Date(primerEvento.finEvento).toLocaleString() : "Fecha por definir"}
                  </p>
                  <p className="text-gray-500 mt-1">
                    {primerEvento.invitados?.length ? `Invitados: ${primerEvento.invitados.map(i => i.nombre).join(", ")}` : "Sin invitados por ahora"}
                  </p>
                </div>
                <div className="flex flex-col gap-2 items-end sm:flex-row sm:justify-end sm:items-center">
                  <p className="font-medium text-sm text-gray-600 bg-blue-100 oscuroBox p-2 rounded-md">
                    {primerEvento.categoria}
                  </p>
                  <p className="font-medium text-sm text-white bg-blue-400 oscuroBox p-2 rounded-md">
                    {Number(primerEvento.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}€
                  </p>
                  <Link
                    to={`/evento/${primerEvento.id}`}
                    className=" bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center"
                  >
                    Detalles
                  </Link>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      
     {/* Cuadrícula de Eventos Restantes */}
      <div className=" max-w-7/8 mx-auto grid md:grid-cols-2 lg:grid-cols-3 gap-5 p-5 sm:p-10 ">
        {restoEventos.map((entrada) => (
          <div key={entrada.id} className="claro oscuro shadow-lg overflow-hidden hover:scale-101 transition transform group h-full ">
            <Link to={`/evento/${entrada.id}`}>
              <div className="relative w-full h-80 ">
                <img
                  src={getImageSrc(entrada.imagenPrincipalUrl)}
                  alt={entrada.nombre}
                  className="w-full h-80 object-cover transition duration-500 group-hover:opacity-70"
                />
                <div className="absolute inset-0 bg-blue-500 opacity-0 group-hover:opacity-20 transition duration-500"></div>
              </div>
            </Link>
            <div className="p-4 flex flex-col justify-between h-70">
              <h3 className="text-xl font-semibold text-gray-700 oscuro">{entrada.nombre}</h3>
              <div className="my-2">
                <p className="text-gray-500 mt-1"><strong>Localización:</strong> {entrada.localizacion}</p>
                <p className="text-gray-500 mt-1">
                  <strong>Inicio Evento:</strong> {entrada.inicioEvento ? new Date(entrada.inicioEvento).toLocaleString() : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-1">
                  <strong>Final Evento:</strong> {entrada.finEvento ? new Date(entrada.finEvento).toLocaleString() : "Fecha por definir"}
                </p>
                <p className="text-gray-500 mt-1">
                  {entrada.invitados?.length ? `Invitados: ${entrada.invitados.map(i => i.nombre).join(", ")}` : "Sin invitados por ahora"}
                </p>
              </div>
              <div className="flex flex-row gap-2 items-end sm:flex-row sm:justify-end sm:items-center">
                <p className="font-medium text-sm text-gray-600 bg-blue-100 oscuroBox p-2 rounded-md">
                  {entrada.categoria}
                </p>
                <p className="text-sm font-medium text-white bg-blue-400 oscuroBox p-2 rounded-md">
                  {Number(entrada.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}€
                </p>
                <Link
                  to={`/evento/${entrada.id}`}
                  className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center inline-block w-auto"
                >
                  Detalles
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>

    </div>
  );
}