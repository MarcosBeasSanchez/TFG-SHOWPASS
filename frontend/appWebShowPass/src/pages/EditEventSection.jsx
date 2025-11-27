import React, { useEffect, useState } from "react";
import config from "../config/config";

const usuarioId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;

const EditEventSection = () => {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedEventId, setSelectedEventId] = useState("");
    const [message, setMessage] = useState("");
    const [formData, setFormData] = useState({
        nombre: "",
        localizacion: "",
        descripcion: "",
        inicioEvento: "",
        finEvento: "",
        precio: "",
        categoria: "",
        aforo: "",
        imagen: null,
        carrusels: [],
        invitados: [],
        vendedorId: ""
    });

    useEffect(() => {
        fetchEventos();
    }, []);

    const getImageSrc = (img) => {
        if (!img) return ""; // si no hay imagen, devolvemos vacío
        if (img instanceof File) return URL.createObjectURL(img); // Nuevo archivo
        if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
        if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
        if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
        return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
    };

    /**
   * Convierte un objeto File a una cadena Base64 (data:image/...).
   * Devuelve una Promise que resuelve la cadena Base64 o la cadena original si no es un File.
   */
    const fileToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            if (!file || !(file instanceof File)) {
                // Si ya es una URL/Base64 string existente (o null), la devolvemos sin cambios.
                resolve(file);
                return;
            }
            const reader = new FileReader();
            // Usamos readAsDataURL para obtener el prefijo 'data:image/...' que el backend puede manejar.
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = (error) => reject(error);
        });
    };

    // Función para obtener todos los eventos
    const fetchEventos = async () => {
        setLoading(true);

        try {
            const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/findAllEventosCreados/${usuarioId}`);
            if (!res.ok) throw new Error("Error al obtener eventos");
            const data = await res.json();
            setEvents(data);
            console.log("Eventos cargados:", data);
        } catch (err) {
            console.error(err);
            setEvents([]);
        } finally {
            setLoading(false);
        }
    };
    // Manejar selección de evento
    const handleEventSelect = (e) => {
        const eventId = e.target.value;
        setSelectedEventId(eventId);
        const event = events.find((evt) => String(evt.id) === eventId);
        if (event) {
            setFormData({
                nombre: event.nombre || "",
                localizacion: event.localizacion || "",
                descripcion: event.descripcion || "",
                inicioEvento: event.inicioEvento || "",
                finEvento: event.finEvento || "",
                precio: event.precio || "",
                categoria: event.categoria || "",
                aforo: event.aforo || "",
                imagen: event.imagenPrincipalUrl || null, 
                carrusels: event.imagenesCarruselUrls || [],
                invitados: event.invitados || [],
                vendedorId: event.vendedorId || ""
            });
            console.log("Evento seleccionado:", event);
        }
    };
    // Manejar cambios en el formulario
    const handleChange = (e) => {
        const { name, value, files } = e.target;
        if (files) {
            if (name === "imagen") {
                // Guarda el nuevo objeto File (esto sobreescribirá la URL anterior si existía)
                setFormData((prev) => ({ ...prev, imagen: files[0] }));
            } else if (name === "carrusels") {
                const newFiles = Array.from(files);
                setFormData((prev) => ({
                    ...prev,
                    carrusels: [...prev.carrusels, ...newFiles],
                }));
            }
        } else {
            setFormData((prev) => ({
                ...prev,
                [name]: value,
            }));
        }
    };
    // Eliminar imagen del carrusel
    const removeCarruselImage = (index) => {
        const nuevas = [...formData.carrusels];
        nuevas.splice(index, 1);
        setFormData((prev) => ({ ...prev, carrusels: nuevas }));
    };
    // Agregar invitado
    const agregarInvitado = () => {
        setFormData((prev) => ({
            ...prev,
            invitados: [
                ...prev.invitados,
                { nombre: "", apellidos: "", descripcion: "", fotoURL: null },
            ],
        }));
    };
    // Eliminar invitado
    const eliminarInvitado = (index) => {
        const nuevosInvitados = [...formData.invitados];
        nuevosInvitados.splice(index, 1);
        setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
    };

    const handleInvitadoChange = (index, e) => {
        const { name, value, files } = e.target;
        const nuevosInvitados = [...formData.invitados];

        if (files && files.length > 0) {
            const reader = new FileReader();
            reader.onloadend = () => {
                nuevosInvitados[index] = {
                    ...nuevosInvitados[index],
                    [name]: reader.result,
                };
                setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
            };
            reader.readAsDataURL(files[0]);
        } else {
            nuevosInvitados[index] = {
                ...nuevosInvitados[index],
                [name]: value,
            };
            setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
        }
    };

    const updateEvento = async (e) => {
        e.preventDefault();
        if (!selectedEventId) {
            setMessage("⚠️ Selecciona un evento primero");
            return;
        }

        try {
            //  1. Conversión de Imágenes a Base64 (Asíncrona) 

            // 1a: Convierte File a Base64 o mantiene la URL existente
            const imagenBase64OUrl = await fileToBase64(formData.imagen);
            //convierte todos los archivos del carrusel
            const carruselsBase64OUrls = await Promise.all(
                formData.carrusels.map(fileToBase64)
            );

            //  2. Crear payload JSON sin archivos
            const payload = {
                nombre: formData.nombre,
                localizacion: formData.localizacion,
                descripcion: formData.descripcion,
                inicioEvento: formData.inicioEvento,
                finEvento: formData.finEvento,
                precio: formData.precio,
                categoria: formData.categoria,
                aforoMax: formData.aforo,
                imagen: imagenBase64OUrl, 
                imagenesCarruselUrls: carruselsBase64OUrls,
                invitados: formData.invitados,
                vendedorId: usuarioId
            };

            //  3. Mostrar el payload que se enviará
            console.log("Payload JSON que se enviará:", payload);

            //  4. Enviar la solicitud PUT con el payload JSON
            const res = await fetch(
                `${config.apiBaseUrl}/tfg/evento/update/${selectedEventId}`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(payload),
                }
            );

            if (!res.ok) throw new Error(`Error al actualizar evento: ${res.statusText}`);

            //  5. Manejar la respuesta del servidor
            const data = await res.json();
            console.log("Respuesta del servidor:", data);
            setMessage("✅ Evento actualizado correctamente");
            fetchEventos();
        } catch (err) {
            console.error(err);
            setMessage("❌ Error al actualizar evento");
        }
    };

    return (
        <div className="mt-4 text-gray-700">
            <h2 className="text-2xl font-bold oscuroTextoGris mb-6">
                Editar Evento
            </h2>

            {loading ? (
                <p className="text-gray-500">Cargando eventos...</p>
            ) : (
                <div className="mb-6">
                    <label className="block mb-2 font-semibold text-gray-500">
                        Selecciona un evento:
                    </label>
                    <select
                        value={selectedEventId}
                        onChange={handleEventSelect}
                        className="w-full p-2 rounded focus:ring-2 bg-gray-100 focus:ring-blue-400 outline-none oscuroBox "
                    >
                        <option value="">-- Selecciona un evento --</option>
                        {events.map((event) => (
                            <option key={event.id} value={event.id}>
                                {event.nombre}
                            </option>
                        ))}
                    </select>
                </div>
            )}

            {selectedEventId && (
                <form
                    onSubmit={updateEvento}
                    className="flex flex-col bg-white shadow p-6 rounded-lg gap-6 oscuro"
                >
                    {/*  Nombre / Localización */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label className="block font-semibold mb-1">Nombre</label>
                            <input
                                type="text"
                                name="nombre"
                                value={formData.nombre}
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox"
                            />
                        </div>
                        <div>
                            <label className="block font-semibold mb-1">Localización</label>
                            <input
                                type="text"
                                name="localizacion"
                                value={formData.localizacion}
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox"
                            />
                        </div>
                    </div>

                    {/*  Descripción */}
                    <div>
                        <label className="block font-semibold mb-1">Descripción</label>
                        <textarea
                            name="descripcion"
                            value={formData.descripcion}
                            onChange={handleChange}
                            className="w-full h-32 p-2 rounded bg-gray-100 oscuroBox"
                        />
                    </div>

                    {/*  Inicio / Fin */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label className="block font-semibold mb-1">Inicio</label>
                            <input
                                type="datetime-local"
                                name="inicioEvento"
                                value={formData.inicioEvento}
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox"
                            />
                        </div>
                        <div>
                            <label className="block font-semibold mb-1">Fin</label>
                            <input
                                type="datetime-local"
                                name="finEvento"
                                value={formData.finEvento}
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox"
                            />
                        </div>
                    </div>

                    {/*  Precio / Categoría */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 oscuro">
                        <div>
                            <label className="block font-semibold mb-1">Precio</label>
                            <input
                                type="number"
                                name="precio"
                                value={formData.precio}
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox"
                            />
                        </div>
                        <div>
                            <label className="block font-semibold mb-1">Categoría</label>
                            <select
                                name="categoria"
                                value={formData.categoria}
                                onChange={handleChange}
                                className="w-full  p-2 rounded bg-gray-100 oscuroBox"
                            >
                                <option value="">Selecciona una categoría</option>
                                <option value="MUSICA">Música</option>
                                <option value="DEPORTE">Deporte</option>
                                <option value="ARTE">Arte</option>
                                <option value="VIDEOJUEGOS">Videojuegos</option>
                                <option value="OTROS">Otros</option>
                            </select>
                        </div>
                    </div>


                    {/*  Aforo */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 oscuro">
                        <div>
                            <label className="block font-semibold mb-1">Aforo máximo</label>
                            <input
                                type="number"
                                name="aforo"
                                value={formData.aforo}
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox"
                            />
                        </div>
                    </div>

                    {/* Imagen principal y carrusel (Sección solicitada) */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

                        {/* Imagen principal */}
                        <div>
                            <label className="block font-semibold mb-1">
                                Imagen principal
                            </label>
                            <input
                                type="file"
                                name="imagen"
                                accept=".png, .jpg, .jpeg, image/png, image/jpeg"
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox cursor-pointer"
                            />
                            {/* PREVISUALIZACIÓN DE IMAGEN PRINCIPAL */}
                            {formData.imagen && (
                                <img
                                    //  Usa la función getImageSrc para manejar File o String
                                    src={getImageSrc(formData.imagen)}
                                    alt="Preview"
                                    className="w-40 h-40 object-cover rounded border mt-3"
                                />
                            )}
                        </div>

                        {/* Carrusel de imágenes */}
                        <div>
                            <label className="block font-semibold mb-1">
                                Carrusel de imágenes
                            </label>
                            <input
                                type="file"
                                name="carrusels"
                                multiple
                                accept=".png, .jpg, .jpeg, image/png, image/jpeg"
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox cursor-pointer"
                            />
                            <div className="flex flex-wrap gap-3 mt-3">
                                {/* PREVISUALIZACIÓN DEL CARRUSEL */}
                                {formData.carrusels.map((imgOrFile, idx) => (
                                    <div key={idx} className="relative w-24 h-24">
                                        <img
                                            src={getImageSrc(imgOrFile)} 
                                            alt={`Carrusel ${idx}`}
                                            className="w-full h-full object-cover rounded border"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => removeCarruselImage(idx)}
                                            className="absolute -top-2 -right-2 bg-red-500 text-white w-6 h-6 rounded-full"
                                        >
                                            ×
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/*  Invitados */}
                    <div>
                        <label className="block font-bold text-lg mb-4">Invitados</label>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            {formData.invitados.map((invitado, idx) => (
                                <div
                                    key={idx}
                                    className="relative p-4 rounded-xl oscuro  border-zinc-600 border-1 "
                                >
                                    {/* Botón eliminar */}
                                    <button
                                        type="button"
                                        onClick={() => eliminarInvitado(idx)}
                                        className="absolute top-0 right-3 text-red-500 text-3xl font-bold hover:text-red-700"
                                    >
                                        ×
                                    </button>

                                    {/* Nombre / Apellidos */}
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 ">
                                        <div>
                                            <label className="block text-sm font-semibold mb-1 ">Nombre</label>
                                            <input
                                                type="text"
                                                name="nombre"
                                                value={invitado.nombre}
                                                onChange={(e) => handleInvitadoChange(idx, e)}
                                                className="w-full p-2 rounded bg-gray-100  oscuroBox outline-none"
                                            />
                                        </div>
                                        <div>
                                            <label className="block text-sm font-semibold mb-1">Apellidos</label>
                                            <input
                                                type="text"
                                                name="apellidos"
                                                value={invitado.apellidos}
                                                onChange={(e) => handleInvitadoChange(idx, e)}
                                                className="w-full p-2 rounded bg-gray-100 oscuroBox outline-none"
                                            />
                                        </div>
                                    </div>

                                    {/* Descripción y Foto */}
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        {/* Imagen del invitado */}
                                        <div className="mt-4">
                                            <label className="block text-sm font-semibold mb-1">Foto del invitado</label>
                                            <input
                                                type="file"
                                                name="fotoURL"
                                                accept=".png, .jpg, .jpeg, image/png, image/jpeg"
                                                onChange={(e) => handleInvitadoChange(idx, e)}
                                                className="w-full p-2 rounded bg-gray-100 oscuroBox cursor-pointer"
                                            />
                                            {invitado.fotoURL && (
                                                <img
                                                    src={getImageSrc(invitado.fotoURL)}
                                                    alt="Invitado"
                                                    className="w-20 h-20 object-cover rounded  mt-2"
                                                />
                                            )}
                                        </div>
                                        {/* Descripción */}
                                        <div className="mt-4">
                                            <label className="block text-sm font-semibold mb-1">Descripción</label>
                                            <input
                                                type="text"
                                                name="descripcion"
                                                value={invitado.descripcion}
                                                onChange={(e) => handleInvitadoChange(idx, e)}
                                                className="w-full  p-2 rounded bg-gray-100  oscuroBox outline-none"
                                            />
                                        </div>
                                    </div>
                                </div>
                            ))}

                            {/* Botón agregar invitado */}
                            <button
                                type="button"
                                onClick={agregarInvitado}
                                className="bg-gray-500 text-white mt-6 p-2 rounded hover:bg-blue-600 transition w-40 h-fit"
                            >
                                + Agregar invitado
                            </button>
                        </div>
                    </div>


                    {/*  Guardar */}
                    <div className="flex justify-end">
                        <button
                            type="submit"
                            className="bg-green-500 text-white p-2 rounded  hover:bg-green-600  mt-4 w-fit  self-end transition"
                        >
                            Guardar cambios
                        </button>
                    </div>

                    {message && (
                        <p className="text-center text-gray-500 font-medium">{message}</p>
                    )}
                </form>
            )}
        </div>
    );
};

export default EditEventSection;
