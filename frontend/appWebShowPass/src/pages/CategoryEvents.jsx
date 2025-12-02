
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import config from "../config/config";

/**
 * Componente que lista todos los eventos asociados a una categoría específica.
 */
export default function CategoriaEventos() {

// ----------------------------------------------------
// // 1. ESTADO Y PARÁMETROS DE LA URL
// ----------------------------------------------------

// Extrae el parámetro 'nombre' (la categoría) de la URL (ej: /categoria/musica).
  const { nombre } = useParams(); 
  // Estado para almacenar la lista de eventos recibida del backend.
  const [eventos, setEventos] = useState([]);
  // Estado booleano para gestionar el estado de carga de los datos.
  const [loading, setLoading] = useState(true);

// ----------------------------------------------------
// 2. LÓGICA DE IMAGEN
// ----------------------------------------------------

    /**
     * Determina la fuente (src) correcta para la etiqueta <img>,
     * manejando diferentes formatos de URL/data (Base64, URL externa, ruta relativa).
     * @param {string} img - URL, ruta, o cadena Base64 de la imagen.
     * @returns {string} La URL o Data URL lista para usar en la etiqueta <img>.
     */
  const getImageSrc = (img) => {
    if (!img) return "/placeholder.jpg"; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo  no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend

    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };

  // ----------------------------------------------------
  // 3. EFECTOS: Carga de Eventos por Categoría
  // ----------------------------------------------------

  // Se ejecuta al montar el componente y cada vez que el 'nombre' de la categoría cambia.
  useEffect(() => {
    const fetchEventos = async () => {
      try {
        // Petición GET al endpoint de filtrado por categoría, usando el nombre de la URL.
        const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findByCategoria/${nombre}`);
        if (!res.ok) throw new Error("Error al obtener eventos");
        const data = await res.json();
        setEventos(data); // Actualiza el estado con los eventos recibidos.
      } catch (err) {
        console.error("❌ Error cargando eventos:", err);
        setEventos([]); // En caso de error, dejamos la lista vacía.
      } finally {
        setLoading(false); // Finaliza el estado de carga.
      }
    };
    fetchEventos(); // Llama a la función de carga de eventos.
  }, [nombre]); // Dependencia: se vuelve a ejecutar si cambia 'nombre'.

  // ----------------------------------------------------
  // 4. RENDERIZADO
  // ----------------------------------------------------
  
  // Muestra mensaje de carga
  if (loading) {
    return <p className="text-center mt-10 text-gray-500">Cargando eventos...</p>;
  }
// Muestra mensaje si no hay eventos en la categoría
  if (eventos.length === 0) {
    return <p className="text-center mt-10 text-gray-500">No hay eventos en la categoría <strong>{nombre}</strong>.</p>;
  }


  // ----------------------------------------------------
  // 5. RENDERIZADO PRINCIPAL (Lista de Eventos)
  // ----------------------------------------------------

  return (
    <div className="container mx-auto px-4 py-8 ">
      {/* Título de la categoría */}
      <h2 className="text-2xl font-bold mb-6 text-center text-gray-600 oscuroTextoGris ">
        {nombre}
      </h2>

      {/* Grid para mostrar las tarjetas de eventos */}
      <div className="grid grid-cols-1  sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 ">
        {eventos.map((evento) => (
          <div
            key={evento.id}
            className=" text-gray-700 rounded-xl shadow-md hover:shadow-lg transition "
          >
            {/* Imagen del evento */}
            <img
              src={getImageSrc(evento.imagenPrincipalUrl)}
              alt={evento.nombre}
              className="w-full h-80 object-cover"
            />
            <div className="flex flex-col justify-between h-100 º p-4 fondoClaro oscuro">

             {/* Contenido de la tarjeta */}
              <div className="">
                <h3 className="text-xl font-semibold mb-2">{evento.nombre}</h3>
                <p className="text-gray-500 mb-2 text-sm line-clamp-10 ">{evento.descripcion}</p>
              </div>
              
              {/* Sección central: Fechas y Precio */}
              <div className="flex flex-row justify-between items-center my-2">
                <div>
                  <p className="text-sm text-gray-600">
                    Inicio: {new Date(evento.inicioEvento).toLocaleString()}
                  </p>
                  <p className="text-sm text-gray-600">
                    Fin: {new Date(evento.finEvento).toLocaleString()}
                  </p>
                </div>
                {/* Muestra el precio formateado a moneda local (€) */}
                <p className=" text-sm font-medium text-gray-600 bg-blue-100 oscuroBox p-1 rounded-md">
                  {Number(evento.precio).toLocaleString("es-ES", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}€
                </p> 
              </div>

              {/* Sección inferior: Botón de detalles */}
              <div className="text-end">
                {/* Enlace directo a la página de detalles del evento */}
                <Link
                  to={`/evento/${evento.id}`}
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