import React, { useEffect, useState } from "react";
import config from "../config/config";

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
        imagen: null,
        carrusels: [],
        invitados: [],
    });

    useEffect(() => {
        fetchEventos();
    }, []);

    // Función para obtener todos los eventos
    const fetchEventos = async () => {
        setLoading(true);
        try {
            const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findAll`);
            if (!res.ok) throw new Error("Error al obtener eventos");
            const data = await res.json();
            setEvents(data);
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
                imagen: null,
                carrusels: [],
                invitados: event.invitados || [],
            });
        }
    };
    // Manejar cambios en el formulario
    const handleChange = (e) => {
        const { name, value, files } = e.target;
        if (files) {
            if (name === "imagen") {
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
            // 🔹 Crear payload JSON sin archivos
            const payload = {
                nombre: formData.nombre,
                localizacion: formData.localizacion,
                descripcion: formData.descripcion,
                inicioEvento: formData.inicioEvento,
                finEvento: formData.finEvento,
                precio: formData.precio,
                categoria: formData.categoria,
                imagen: formData.imagen ? URL.createObjectURL(formData.imagen) : null, // Mantener URL o null
                carrusels: formData.carrusels.map(file => URL.createObjectURL(file)), // Solo URLs de preview si existen
                invitados: formData.invitados,
            };

            // 🔹 Mostrar el payload que se enviará
            console.log("Payload JSON que se enviará:", payload);

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

            if (!res.ok) throw new Error("Error al actualizar evento");
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
                    {/* 🔸 Nombre / Localización */}
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

                    {/* 🔸 Descripción */}
                    <div>
                        <label className="block font-semibold mb-1">Descripción</label>
                        <textarea
                            name="descripcion"
                            value={formData.descripcion}
                            onChange={handleChange}
                            className="w-full h-32 p-2 rounded bg-gray-100 oscuroBox"
                        />
                    </div>

                    {/* 🔸 Inicio / Fin */}
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

                    {/* 🔸 Precio / Categoría */}
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

                    {/* 🔸 Imagen principal y carrusel */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

                        <div>
                            <label className="block font-semibold mb-1">
                                Imagen principal
                            </label>
                            <input
                                type="file"
                                name="imagen"
                                accept="image/*"
                                onChange={handleChange}
                                className="w-full  p-2 rounded bg-gray-100 oscuroBox cursor-pointer"
                            />
                            {formData.imagen && (
                                <img
                                    src={URL.createObjectURL(formData.imagen)}
                                    alt="Preview"
                                    className="w-40 h-40 object-cover rounded border mt-3"
                                />
                            )}
                        </div>

                        <div>
                            <label className="block font-semibold mb-1">
                                Carrusel de imágenes
                            </label>
                            <input
                                type="file"
                                name="carrusels"
                                multiple
                                accept="image/*"
                                onChange={handleChange}
                                className="w-full p-2 rounded bg-gray-100 oscuroBox cursor-pointer"
                            />
                            <div className="flex flex-wrap gap-3 mt-3">
                                {formData.carrusels.map((file, idx) => (
                                    <div key={idx} className="relative w-24 h-24">
                                        <img
                                            src={URL.createObjectURL(file)}
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

                    {/* 🔸 Invitados */}
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
                                                accept="image/*"
                                                onChange={(e) => handleInvitadoChange(idx, e)}
                                                className="w-full p-2 rounded bg-gray-100 oscuroBox cursor-pointer"
                                            />
                                            {invitado.fotoURL && (
                                                <img
                                                    src={invitado.fotoURL}
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


                    {/* 🔹 Guardar */}
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
