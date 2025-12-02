import { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import config from "../config/config";


/**
 * Componente que muestra los resultados de la búsqueda de eventos
 * basándose en el parámetro 'query' de la URL.
 */
export default function BusquedaEventos() {

    // ----------------------------------------------------
    // 1. ESTADO Y HOOKS
    // ----------------------------------------------------

    // Estado para almacenar la lista de eventos recibida del backend.
    const [entradas, setEntradas] = useState([]);
    // Hook para acceder al objeto de ubicación actual, que contiene la URL.
    const location = useLocation();

    // Extraer el parámetro 'query' de la cadena de consulta (e.g., ?query=concierto).
    const params = new URLSearchParams(location.search);
    // Guarda el valor de 'query' o una cadena vacía si no existe.
    const busqueda = params.get("query") || "";


    // ----------------------------------------------------
    // 2. FETCHING DE DATOS (al cargar el componente o cambiar la búsqueda)
    // ----------------------------------------------------

    // useEffect se ejecuta cada vez que el valor de 'busqueda' cambia.
    useEffect(() => {
        const fetchEventos = async () => {
            try {
                // Realiza la petición GET al endpoint de filtrado del backend.
                // Importante: Se utiliza 'encodeURIComponent' para asegurar que el texto de búsqueda se envíe correctamente.
                const res = await fetch(`${config.apiBaseUrl}/tfg/evento/filterByBusqueda?nombre=` + encodeURIComponent(busqueda));
                const data = await res.json(); //
                setEntradas(data); // Actualiza el estado con los eventos recibidos.
            } catch (err) {
                console.error("Error cargando eventos:", err);
            }
        };
        fetchEventos(); // Llama a la función para obtener los eventos.
    }, [busqueda]) // Dependencia: re-ejecuta el efecto si el término de búsqueda cambia.


// ----------------------------------------------------
// 3. LÓGICA DE IMAGEN (Determinación de la fuente de la imagen)
// ----------------------------------------------------

    /**
     * Determina la fuente (src) correcta para la etiqueta <img>,
     * manejando diferentes formatos de URL/data (Base64, URL externa, ruta relativa).
     * @param {string} img - URL, ruta, o cadena Base64 de la imagen.
     * @returns {string} La URL o Data URL lista para usar en la etiqueta <img>.
     */

    const getImageSrc = (img) => {
        if (!img) return ""; // si no hay imagen, devolvemos vacío
        if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
        if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
        if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
        return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
    };

    return (
        <div className="py-8 max-w-7/8 mx-auto">
            <h1 className="text-2xl font-bold mb-4 text-gray-600 text-center oscuroTextoGris">RESULTADOS DE BÚSQUEDA</h1>

            {/* Mensaje que muestra el término de búsqueda actual */}
            <div>
                <p className="text-sm px-4 sm:text-lg text-gray-500 mb-4 text-center ">
                    Mostrando resultados para <span className="font-semibold text-blue-500">"{busqueda}"</span>
                </p>
            </div>

            <div className="flex flex-col gap-6 [@media(min-width:978px)]:gap-10 [@media(min-width:978px)]:p-10">
                
                {/* Renderizado condicional: Si hay eventos, los lista; si no, muestra un mensaje. */}
                {entradas.length > 0 ? entradas.map((evento) => (
                    <div
                        key={evento.id}
                        className="bg-white oscuro shadow-lg overflow-hidden hover:scale-101 transition transform group flex flex-col [@media(min-width:978px)]:flex-row"
                    >
                        {/* Enlace para ir a la página de detalles del evento */}
                        <Link to={`/evento/${evento.id}`} className="w-full sm:w-full [@media(min-width:978px)]:w-130 lg:w-120 flex-shrink-0">
                            <div className="w-full h-90 aspect-square overflow-hidden flex items-center justify-center bg-gray-200">
                                <img
                                    src={getImageSrc(evento.imagenPrincipalUrl)}
                                    alt={evento.nombre}
                                    className="w-full h-full object-cover transition duration-500 group-hover:opacity-70"
                                />
                            </div>
                        </Link>

                        {/* Contenido del evento */}
                        <div className="p-4 flex flex-col h-auto flex-1">
                            <h3 className="text-xl font-semibold text-gray-700 oscuroTextoGris">{evento.nombre}</h3>
                            <div className="my-1">
                                <p className="text-gray-500 mt-1"><strong>Localización:</strong> {evento.localizacion}</p>

                                {/* Formato de fecha y hora */}
                                <p className="text-gray-500 mt-1">
                                    <strong>Inicio Evento:</strong> {evento.inicioEvento
                                        ? new Date(evento.inicioEvento).toLocaleString()
                                        : "Fecha por definir"}
                                </p>
                                <p className="text-gray-500 mt-1"><strong>Final Evento:</strong> {evento.finEvento
                                    ? new Date(evento.finEvento).toLocaleString()
                                    : "Fecha por definir"}
                                </p>

                                {/* Lista de invitados */}
                                <p className="text-gray-500 mt-1">
                                    {evento.invitados?.length
                                        ? `Invitados: ${evento.invitados.map(i => i.nombre).join(", ")}`
                                        : "Sin invitados por ahora"}
                                </p>

                                {/* Descripción (limitada a 5 líneas con Tailwind CSS 'line-clamp-5') */}
                                <p className="text-gray-500 mt-1 line-clamp-5 text-sm">
                                    {evento.descripcion || "Sin descripcion del evento"}
                                </p>
                            </div>
                            {/* Precio y enlace a detalles */}
                            <div className="flex justify-end mt-2 items-center gap-2">
                                <span className=" font-medium text-sm text-gray-600 bg-blue-100  p-2 rounded-md oscuroBox">
                                    {Number(evento.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 }) || "sin precio"}€ </span>
                                <Link
                                    to={`/evento/${evento.id}`}
                                    className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center inline-block w-auto"
                                >
                                    Ver detalles
                                </Link>
                            </div>
                        </div>
                    </div>
                )) : (
                    // Bloque que se muestra si no se encuentran resultados
                    <div className="flex flex-col items-center justify-center py-16 bg-white rounded-xl shadow-lg fondoOscuro ">
                        <div className="mb-">
                            {/* Imagen o GIF de "No hay resultados" */}
                            <img
                                src="https://assets-v2.lottiefiles.com/a/f0470cd6-117f-11ee-a4ed-1b2d7fb6aaaf/i83iUdPISg.gif"
                                alt="No results found"
                                className="mx-auto w-56 h-56 "
                            />
                        </div>
                        <h2 className="text-2xl sm:text-2xl font-bold text-blue-500 mb-2 text-center ">¡Ups! No se encontraron eventos</h2>
                        <p className="text-sm px-4 sm:text-lg  text-gray-500 mb-4 text-center max-w-md">
                            No hay resultados para <span className="font-semibold text-blue-500">"{busqueda}"</span>.
                            Prueba con otro nombre o revisa la ortografía.
                        </p>
                        <Link
                            to="/"
                            className="mt-4 px-6 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 transition"
                        >
                            Volver al inicio
                        </Link>
                    </div>
                )}
            </div>
        </div>
    );
}