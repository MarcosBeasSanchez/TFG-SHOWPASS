// Importa hooks esenciales de React (useEffect, useState) y la configuración de la API.
import  { useEffect, useState } from "react";
import config from "../config/config";

/**
 * Componente funcional para el panel de administración de eventos.
 * Permite listar y eliminar todos los eventos de la base de datos.
 */
const AdminEvents = () => {
    // Estado para almacenar la lista de eventos obtenida de la API.
    const [eventos, setEventos] = useState([]);
    // Estado booleano para indicar si los datos están siendo cargados.
    const [loading, setLoading] = useState(true);
    // Estado 'formData': Mantiene la estructura de un evento completo.
    const [formData, setFormData] = useState({
        nombre: "",
        localizacion: "",
        inicioEvento: "",
        finEvento: "",
        descripcion: "",
        precio: "",
        categoria: "",
        imagen: null, // Archivo de la imagen principal
        carrusels: [], // Array de archivos para el carrusel
        invitados: [] // Array de datos anidados (objetos de invitado)
    });

    // Estado para mostrar mensajes de éxito o error (p.ej., después de una eliminación).
    const [message, setMessage] = useState("");

    //----------------------------------------------------
    // 1. EFECTOS (Lifecycle)
    // ----------------------------------------------------
    // useEffect se ejecuta una sola vez al montar el componente (gracias al array de dependencias vacío []).
    // Su propósito es cargar la lista inicial de eventos.
    useEffect(() => {
        fetchEventos();
    }, []);


    /**
     * Función asíncrona para obtener todos los eventos del backend.
     */
    const fetchEventos = async () => {
        setLoading(true); // Indica que la carga está en progreso
        try {
            const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findAll`);
            if (!res.ok) throw new Error("Error al obtener eventos");
            const data = await res.json();
            setEventos(data); // Actualiza el estado con la lista de eventos
        } catch (err) {
            console.error(err);
            setEventos([]); // Limpia la lista en caso de error de conexión/API
        } finally {
            setLoading(false); // Indica que la carga ha finalizado
        }
    };
    
    /**
     * Función asíncrona para eliminar un evento por su ID (CRUD: DELETE).
     * @param {number} id - El ID del evento a eliminar.
     */
    const eliminarEvento = async (id) => {
        setMessage(""); // Limpia mensajes previos
        try {
            const res = await fetch(`${config.apiBaseUrl}/tfg/evento/delete/${id}`, {
                method: "DELETE"
            });
            if (!res.ok) throw new Error("Error al eliminar evento");
            setMessage("✅ Evento eliminado correctamente"); 
            fetchEventos();
        } catch (err) {
            console.error(err);
            setMessage("❌ Error al eliminar evento");
        }
    };

    // ----------------------------------------------------
    // 2. RENDERIZADO (JSX)
    // ----------------------------------------------------
    return (
        <div className="mt-4 px-2 md:px-0 text-gray-500 ">

            {/* Lista de eventos */}
            {loading ? ( // Muestra un mensaje de carga mientras se obtienen los datos
                <p className="text-red-500 text-center">Cargando eventos...</p>
            ) : ( // Muestra la lista de eventos una vez cargados
                <div>
                    <h2 className="text-xl font-bold oscuroTextoGris my-4">Todos los Eventos</h2>
                    <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {eventos.map((evento) => (
                            <div key={evento.id} className="bg-gray-100 p-4 rounded shadow flex flex-col gap-2 text-black oscuroBox">
                                <p className="font-semibold">{evento.nombre}</p>
                                <p>{evento.localizacion}</p>
                                <p>{evento.categoria}</p>
                                <p>{evento.precio} €</p>
                                
                                {/* Botón para eliminar el evento. Llama a la función 'eliminarEvento' con el ID. */}
                                <button onClick={() => eliminarEvento(evento.id)} className="bg-red-500 text-white p-2 rounded hover:bg-red-600 transition mt-2">Eliminar</button>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminEvents;
