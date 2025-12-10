// Importa hooks esenciales de React y la configuración de la API.
import React, { useEffect, useState } from "react";
import config from "../config/config";

// Obtiene el ID del usuario actual (vendedor) desde el localStorage.
// Esto se realiza fuera del componente para que el valor esté disponible inmediatamente
// en la primera renderización sin depender del estado o useEffect.
const usuarioId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;

/**
 * Componente funcional para la sección de añadir un nuevo evento.
 * Solo debe ser accesible por usuarios con rol de VENDEDOR.
 */
const AddEventSection = () => {
    //const [loading, setLoading] = useState(true);

    // Estado principal que almacena todos los datos del formulario.
    const [formData, setFormData] = useState({
        nombre: "",
        localizacion: "",
        inicioEvento: "",
        finEvento: "",
        descripcion: "",
        precio: "",
        categoria: "",
        aforo: "",
        imagen: null,
        carrusels: [],
        invitados: [],
    });

    // Estado para mensajes de éxito o error
    const [message, setMessage] = useState("");

    // ----------------------------------------------------
    // 2. MANEJADORES DE CAMBIOS BÁSICOS
    // ----------------------------------------------------
    // Maneja los cambios de los inputs de texto, números, fechas y la imagen principal (un solo archivo).
    const handleChange = (e) => {
        const { name, value, files } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: files ? files[0] : value
        }));
    };

    // Maneja la adición de múltiples archivos para el carrusel.
    const handleCarruselChange = (e) => {
        const files = Array.from(e.target.files);
        setFormData((prev) => ({
            ...prev,
            carrusels: [...prev.carrusels, ...files]
        }));
    };

    // Maneja la eliminación de una imagen del carrusel por su índice.
    const eliminarImagenCarrusel = (index) => {
        const nuevasImagenes = [...formData.carrusels];
        nuevasImagenes.splice(index, 1); // Elimina el elemento en la posición 'index'
        setFormData((prev) => ({ ...prev, carrusels: nuevasImagenes }));
    };
    // ----------------------------------------------------
    // 3. MANEJADORES DE INVITADOS (Datos Anidados)
    // ---------------------------------------------------- 

    // Maneja el cambio en cualquier campo de un invitado específico.
    const handleInvitadoChange = (index, e) => {
        const { name, value, files } = e.target;
        const nuevosInvitados = [...formData.invitados];

        // Si se carga una foto, se debe convertir a Base64 para previsualizarla o almacenarla.
        if (files) {
            const reader = new FileReader();
            reader.onloadend = () => {
                // Actualiza el objeto invitado con el Base64 (Data URL) de la foto.
                nuevosInvitados[index] = {
                    ...nuevosInvitados[index],
                    [name]: reader.result // Data URL de la imagen
                };
                setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
            };
            reader.readAsDataURL(files[0]);
        } else {
            nuevosInvitados[index] = {
                ...nuevosInvitados[index],
                [name]: value
            };
            setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
        }
    };

    // Añade un nuevo objeto de invitado vacío al array `formData.invitados`.
    const agregarInvitado = () => {
        setFormData((prev) => ({
            ...prev,
            invitados: [...prev.invitados, { nombre: "", apellidos: "", fotoURL: null }]
        }));
    };

    // Elimina un invitado del array por su índice.
    const eliminarInvitado = (index) => {
        const nuevosInvitados = [...formData.invitados];
        nuevosInvitados.splice(index, 1);
        setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
    };

    // ----------------------------------------------------
    // 4. CREACIÓN DEL EVENTO (Envío al Backend)
    // ----------------------------------------------------

    const crearEvento = async (e) => {
        e.preventDefault();
        setMessage("");

        try {
            // 1. Validación básica
            if (!usuarioId) {
                alert("No se encontró el ID del usuario (vendedor).");
                return;
            }

            // 2. Inicializar el objeto FormData para manejar archivos y datos
            const payload = new FormData();

            // --- CONVERSIÓN DE CAMPOS NUMÉRICOS A NÚMERO ---
            // Se asegura que "precio" y "aforo" se envíen como números.
            const precioNumero = Number(formData.precio);
            const aforoMaximo = Number(formData.aforo);

            // 3. Adjuntar todos los datos al FormData
            payload.append("vendedorId", usuarioId); // IMPORTANTE

            // Campos de texto y fechas
            payload.append("nombre", formData.nombre);
            payload.append("localizacion", formData.localizacion);
            payload.append("inicioEvento", formData.inicioEvento);
            payload.append("finEvento", formData.finEvento);
            payload.append("descripcion", formData.descripcion);
            payload.append("categoria", formData.categoria);

            // Campos numéricos (ya convertidos)
            payload.append("precio", precioNumero);
            payload.append("aforoMax", aforoMaximo);

            // Archivos
            if (formData.imagen) {
                payload.append("imagen", formData.imagen); // Imagen principal
            }

            // Múltiples archivos de carrusel (el backend debe procesar esta lista)
            formData.carrusels.forEach((file) => payload.append("carrusels", file));

            // Datos anidados: Invitados (debe enviarse como cadena JSON)
            payload.append("invitados", JSON.stringify(formData.invitados));

            // 4. Envío de la petición
            const res = await fetch(`${config.apiBaseUrl}/tfg/evento/insert`, {
                method: "POST",
                body: payload, // FormData se envía directamente como body
                // No se establece Content-Type, el navegador lo hace automáticamente (multipart/form-data)
            });

            // 5. Manejo de errores
            if (!res.ok) {
                const errorData = await res.json();
                // Lanza un error más específico si el servidor proporciona un mensaje
                throw new Error(`Error ${res.status}: ${errorData.message || res.statusText}`);
            }

            // 6. Éxito: Muestra mensaje y resetea el formulario.
            setMessage("✅ Evento creado correctamente");
            setFormData({
                nombre: "",
                localizacion: "",
                inicioEvento: "",
                finEvento: "",
                descripcion: "",
                precio: "",
                categoria: "",
                aforo: "", // Usamos el nombre original del estado 'aforo'
                imagen: null,
                carrusels: [],
                invitados: [],
            });

            console.log("Evento creado y formulario reseteado.");
            //fetchEventos();
        } catch (err) {
            console.error(err);
            setMessage("❌ Error al crear evento: " + (err.message || "Error desconocido"));
        }
    };

    // ----------------------------------------------------
    // 5. RENDERIZADO
    // ----------------------------------------------------
    return (
        <div className="mt-4 px-2 md:px-0 text-gray-500 ">
            <h2 className="text-2xl font-bold oscuroTextoGris mb-4">Crear Evento</h2>

            {/* Formulario para crear evento */}
            <form onSubmit={crearEvento} className="flex flex-col bg-white p-4 rounded  mb-4 gap-4 oscuro">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 items-center">
                    <label>Nombre:</label>
                    <input type="text" name="nombre" value={formData.nombre} maxLength={99} onChange={handleChange} required className="p-2  rounded bg-gray-200 text-black oscuroBox " />

                    <label >Localización:</label>
                    <input type="text" name="localizacion" value={formData.localizacion} maxLength={50} onChange={handleChange} required className="p-2  rounded bg-gray-200 text-black oscuroBox" />

                    <label>Inicio Evento:</label>
                    <input type="datetime-local" name="inicioEvento" value={formData.inicioEvento} onChange={handleChange} required className="p-2  rounded  bg-gray-200 text-black oscuroBox" />

                    <label>Fin Evento:</label>
                    <input type="datetime-local" name="finEvento" value={formData.finEvento} onChange={handleChange} required className="p-2  rounded  bg-gray-200 text-black oscuroBox" />

                    <label>Precio:</label>
                    <input type="number" name="precio" value={formData.precio} onChange={handleChange} min="0" step="0.01" className="p-2  rounded  bg-gray-200 text-black oscuroBox" />

                    <label>Aforo máximo:</label>
                    <input type="number" name="aforo" value={formData.aforo} onChange={handleChange} min="0" className="p-2  rounded  bg-gray-200 text-black oscuroBox" />

                    <label>Categoría:</label>
                    <select name="categoria" value={formData.categoria} onChange={handleChange} required className="p-2  rounded bg-gray-200 text-black oscuroBox">
                        <option value="">Selecciona categoría</option>
                        <option value="MUSICA">Música</option>
                        <option value="DEPORTES">Deportes</option>
                        <option value="ARTE">Arte</option>
                        <option value="VIDEOJUEGOS">Videojuegos</option>
                        <option value="OTROS">Otros</option>
                    </select>

                    <label>Imagen principal:</label>
                    <input type="file" name="imagen" accept=".png, .jpg, .jpeg, image/png, image/jpeg" onChange={handleChange} className="p-2  bg-gray-200 rounded text-black oscuroBox" />

                    <label>Descripción:</label>
                    <textarea name="descripcion" value={formData.descripcion} onChange={handleChange} className="h-32 p-2  bg-gray-200 rounded text-black md:col-span-2 oscuroBox" />

                    <label>Carrusel de imágenes:</label>
                    <input type="file" multiple accept=".png, .jpg, .jpeg, image/png, image/jpeg" onChange={handleCarruselChange} className="w-1/2 p-2 bg-gray-200 rounded text-black md:col-span-2 oscuroBox" />
                    <div className="flex flex-wrap gap-2 md:col-span-2">
                        {formData.carrusels.map((file, idx) => (
                            <div key={idx} className="relative w-24 h-24">
                                <img src={URL.createObjectURL(file)} alt={`Carrusel ${idx}`} className="w-full h-full object-cover rounded border" />
                                <button type="button" onClick={() => eliminarImagenCarrusel(idx)} className="absolute top-0 right-0 bg-red-500 text-white rounded-full w-6 h-6">×</button>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Sección de Invitados (Datos Anidados) */}
                <h3 className="font-bold ">Invitados</h3>
                {formData.invitados.map((invitado, idx) => (

                    <div key={idx} className="flex lg:flex-row flex-col  gap-2 items-center justify- oscuro">
                        <input type="text" name="nombre" placeholder="Nombre" value={invitado.nombre} maxLength={50} onChange={(e) => handleInvitadoChange(idx, e)} className="w-full  p-2  rounded text-black oscuroBox bg-gray-200" />
                        <input type="text" name="apellidos" placeholder="Apellidos" value={invitado.apellidos} maxLength={50} onChange={(e) => handleInvitadoChange(idx, e)} className="w-full p-2  rounded text-black oscuroBox bg-gray-200" />
                        <div className="flex flex-row gap-2 my-3.5 lg:my-0 items-center justify-center lg:justify-start ">
                            <label className=" bg-blue-500 text-white text-center px-4 py-2 rounded">
                                Foto <input type="file" name="fotoURL" onChange={(e) => handleInvitadoChange(idx, e)} className="hidden" />
                            </label>
                            <button type="button" onClick={() => eliminarInvitado(idx)} className=" bg-red-500 text-white p-2 rounded hover:bg-red-600 transition">Quitar</button>
                        </div>

                    </div>
                ))}

                {/* Botón para añadir una nueva fila de invitado */}
                <button type="button"
                    onClick={agregarInvitado}
                    className="bg-gray-500 text-white mt-6 p-2 rounded hover:bg-blue-600 transition w-40">
                    + Agregar invitado
                </button>

                {/* Botón de Submit */}
                <div className="flex flex-col gap-4 mt-4 items-end">
                    <button
                        type="submit"
                        className="bg-green-500 text-white p-2 rounded  hover:bg-green-600 transition mt-4 w-full md:w-30 self-end">
                        Crear evento
                    </button>
                </div>
            </form>
            {/* Mensaje de estado (éxito/error) */}
            {message && <p className="text-center mb-4 text-gray-500">{message}</p>}
        </div>
    );
};

export default AddEventSection;
