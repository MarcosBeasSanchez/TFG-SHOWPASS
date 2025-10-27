import React, { useEffect, useState } from "react";
import config from "../config/config";

const usuarioId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;


const AddEventSection = () => {
    const [eventos, setEventos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [formData, setFormData] = useState({
        nombre: "",
        localizacion: "",
        inicioEvento: "",
        finEvento: "",
        descripcion: "",
        precio: "",
        categoria: "",
        imagen: null,
        carrusels: [],
        invitados: [],
    });
    const [message, setMessage] = useState("");

    useEffect(() => {
        fetchEventos();
    }, []);

    const fetchEventos = async () => {
        setLoading(true);
        try {
            const res = await fetch(`${config.apiBaseUrl}/tfg/evento/findAll`);
            if (!res.ok) throw new Error("Error al obtener eventos");
            const data = await res.json();
            setEventos(data);
        } catch (err) {
            console.error(err);
            setEventos([]);
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value, files } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: files ? files[0] : value
        }));
    };

    const handleCarruselChange = (e) => {
        const files = Array.from(e.target.files);
        setFormData((prev) => ({
            ...prev,
            carrusels: [...prev.carrusels, ...files]
        }));
    };

    const eliminarImagenCarrusel = (index) => {
        const nuevasImagenes = [...formData.carrusels];
        nuevasImagenes.splice(index, 1);
        setFormData((prev) => ({ ...prev, carrusels: nuevasImagenes }));
    };

    const handleInvitadoChange = (index, e) => {
        const { name, value, files } = e.target;
        const nuevosInvitados = [...formData.invitados];

        if (files) {
            const reader = new FileReader();
            reader.onloadend = () => {
                nuevosInvitados[index] = {
                    ...nuevosInvitados[index],
                    [name]: reader.result
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

    const agregarInvitado = () => {
        setFormData((prev) => ({
            ...prev,
            invitados: [...prev.invitados, { nombre: "", apellidos: "", fotoURL: null }]
        }));
    };

    const eliminarInvitado = (index) => {
        const nuevosInvitados = [...formData.invitados];
        nuevosInvitados.splice(index, 1);
        setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
    };

    const crearEvento = async (e) => {
        e.preventDefault();
        setMessage("");

        try {
            const payload = new FormData();
            //Error si no hay usuarioId
            if (!usuarioId) {
                alert("No se encontró el ID del usuario (vendedor).");
                return;
            }
            // Agregar el vendedorId al payload
            payload.append("vendedorId", usuarioId); // 👈 IMPORTANTE
            // Resto de campos
            payload.append("nombre", formData.nombre);
            payload.append("localizacion", formData.localizacion);
            payload.append("inicioEvento", formData.inicioEvento);
            payload.append("finEvento", formData.finEvento);
            payload.append("descripcion", formData.descripcion);
            payload.append("precio", formData.precio);
            payload.append("categoria", formData.categoria);

            if (formData.imagen) payload.append("imagen", formData.imagen);
            formData.carrusels.forEach((file) => payload.append("carrusels", file));
            payload.append("invitados", JSON.stringify(formData.invitados));

            const res = await fetch(`${config.apiBaseUrl}/tfg/evento/insert`, {
                method: "POST",
                body: payload,
            });

            if (!res.ok) throw new Error("Error al crear evento");

            setMessage("✅ Evento creado correctamente");
            setFormData({
                nombre: "",
                localizacion: "",
                inicioEvento: "",
                finEvento: "",
                descripcion: "",
                precio: "",
                categoria: "",
                imagen: null,
                carrusels: [],
                invitados: [],
            });
            
            console.log("Envio al backend" + JSON.stringify(payload));
            //fetchEventos();
        } catch (err) {
            console.error(err);
            setMessage("❌ Error al crear evento");
        }
    };


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
                    <input type="number" name="precio" value={formData.precio} onChange={handleChange} min="0" className="p-2  rounded  bg-gray-200 text-black oscuroBox" />

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
                    <input type="file" name="imagen" onChange={handleChange} className="p-2  bg-gray-200 rounded text-black oscuroBox" />

                    <label>Descripción:</label>
                    <textarea name="descripcion" value={formData.descripcion} onChange={handleChange} className="h-32 p-2  bg-gray-200 rounded text-black md:col-span-2 oscuroBox" />

                    <label>Carrusel de imágenes:</label>
                    <input type="file" multiple onChange={handleCarruselChange} className="w-1/2 p-2 bg-gray-200 rounded text-black md:col-span-2 oscuroBox" />
                    <div className="flex flex-wrap gap-2 md:col-span-2">
                        {formData.carrusels.map((file, idx) => (
                            <div key={idx} className="relative w-24 h-24">
                                <img src={URL.createObjectURL(file)} alt={`Carrusel ${idx}`} className="w-full h-full object-cover rounded border" />
                                <button type="button" onClick={() => eliminarImagenCarrusel(idx)} className="absolute top-0 right-0 bg-red-500 text-white rounded-full w-6 h-6">×</button>
                            </div>
                        ))}
                    </div>
                </div>

                <h3 className="font-bold ">Invitados</h3>
                {formData.invitados.map((invitado, idx) => (

                    <div key={idx} className="flex lg:flex-row flex-col  gap-2 items-center justify- oscuro">
                        <input type="text" name="nombre" placeholder="Nombre" value={invitado.nombre} maxLength={50} onChange={(e) => handleInvitadoChange(idx, e)} className="w-full  p-2  rounded text-black oscuroBox" />
                        <input type="text" name="apellidos" placeholder="Apellidos" value={invitado.apellidos} maxLength={50} onChange={(e) => handleInvitadoChange(idx, e)} className="w-full p-2  rounded text-black oscuroBox" />
                        <div className="flex flex-row gap-2 my-3.5 lg:my-0 items-center justify-center lg:justify-start ">
                            <label className=" bg-blue-500 text-white text-center px-4 py-2 rounded">
                                Foto <input type="file" name="fotoURL" onChange={(e) => handleInvitadoChange(idx, e)} className="hidden" />
                            </label>
                            <button type="button" onClick={() => eliminarInvitado(idx)} className=" bg-red-500 text-white p-2 rounded hover:bg-red-600 transition">Quitar</button>
                        </div>

                    </div>
                ))}
                <button type="button"
                    onClick={agregarInvitado}
                    className="bg-gray-500 text-white mt-6 p-2 rounded hover:bg-blue-600 transition w-40">
                    + Agregar invitado
                </button>

                <div className="flex flex-col gap-4 mt-4 items-end">
                    <button
                        type="submit"
                        className="bg-green-500 text-white p-2 rounded  hover:bg-green-600 transition mt-4 w-full md:w-30 self-end">
                        Crear evento
                    </button>
                </div>
            </form>

            {message && <p className="text-center mb-4 text-gray-500">{message}</p>}
        </div>
    );
};

export default AddEventSection;
