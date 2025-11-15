import { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import config from "../config/config";

export default function BusquedaEventos() {
    const [entradas, setEntradas] = useState([]);
    const location = useLocation();

    // Leer el parámetro 'query' de la URL
    const params = new URLSearchParams(location.search);
    const busqueda = params.get("query") || "";

    useEffect(() => {
        const fetchEventos = async () => {
            try {
                const res = await fetch("http://localhost:8080/tfg/evento/filterByNombre?nombre=" + encodeURIComponent(busqueda));
                const data = await res.json();
                setEntradas(data);
            } catch (err) {
                console.error("Error cargando eventos:", err);
            }
        };
        fetchEventos();
    }, []);

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
            <div>
                <p className="text-sm px-4 sm:text-lg text-gray-500 mb-4 text-center ">
                    Mostrando resultados para <span className="font-semibold text-blue-500">"{busqueda}"</span>
                </p>
            </div>

            <div className="flex flex-col gap-6 [@media(min-width:978px)]:gap-10 [@media(min-width:978px)]:p-10">

                {entradas.length > 0 ? entradas.map((evento) => (
                    <div
                        key={evento.id}
                        className="bg-white oscuro shadow-lg overflow-hidden hover:scale-101 transition transform group flex flex-col [@media(min-width:978px)]:flex-row"
                    >
                        <Link to={`/evento/${evento.nombre}`} className="w-full sm:w-full [@media(min-width:978px)]:w-130 lg:w-120 flex-shrink-0">
                            <div className="w-full h-90 aspect-square overflow-hidden flex items-center justify-center bg-gray-200">
                                <img
                                    src={getImageSrc(evento.imagenPrincipalUrl)}
                                    alt={evento.nombre}
                                    className="w-full h-full object-cover transition duration-500 group-hover:opacity-70"
                                />
                            </div>
                        </Link>
                        <div className="p-4 flex flex-col h-auto flex-1">
                            <h3 className="text-xl font-semibold text-gray-700 oscuroTextoGris">{evento.nombre}</h3>
                            <div className="my-1">
                                <p className="text-gray-500 mt-1"><strong>Localización:</strong> {evento.localizacion}</p>
                                <p className="text-gray-500 mt-1">
                                    <strong>Inicio Evento:</strong> {evento.inicioEvento
                                        ? new Date(evento.inicioEvento).toLocaleString()
                                        : "Fecha por definir"}
                                </p>
                                <p className="text-gray-500 mt-1"><strong>Final Evento:</strong> {evento.finEvento
                                    ? new Date(evento.finEvento).toLocaleString()
                                    : "Fecha por definir"}
                                </p>
                                <p className="text-gray-500 mt-1">
                                    {evento.invitados?.length
                                        ? `Invitados: ${evento.invitados.map(i => i.nombre).join(", ")}`
                                        : "Sin invitados por ahora"}
                                </p>
                                <p className="text-gray-500 mt-1 line-clamp-5 text-sm">
                                    {evento.descripcion || "Sin descripcion del evento"}
                                </p>
                            </div>
                            <div className="flex justify-end mt-2 items-center gap-2">
                                <span className=" font-medium text-sm text-gray-600 bg-blue-100  p-2 rounded-md oscuroBox">
                                    {Number(evento.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 }) || "sin precio"}€ </span>
                                <Link
                                    to={`/evento/${evento.nombre}`}
                                    className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 text-center inline-block w-auto"
                                >
                                    Ver detalles
                                </Link>
                            </div>
                        </div>
                    </div>
                )) : (
                    <div className="flex flex-col items-center justify-center py-16 bg-white rounded-xl shadow-lg fondoOscuro ">
                        <div className="mb-">
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