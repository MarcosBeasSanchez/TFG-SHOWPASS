import React, { useEffect, useState } from "react";
import config from "../config/config";

const AdminEvents = () => {
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
        invitados: []
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

    const removerImagenCarrusel = (index) => {
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

    const removerInvitado = (index) => {
        const nuevosInvitados = [...formData.invitados];
        nuevosInvitados.splice(index, 1);
        setFormData((prev) => ({ ...prev, invitados: nuevosInvitados }));
    };


    const eliminarEvento = async (id) => {
        setMessage("");
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

    return (
        <div className="mt-4 px-2 md:px-0 text-gray-500 ">
            {/* Lista de eventos */}
            {loading ? (
                <p className="text-red-500 text-center">Cargando eventos...</p>
            ) : (
                <div>
                    <h2 className="text-xl font-bold oscuroTextoGris my-4">Todos los Eventos</h2>
                    <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {eventos.map((evento) => (
                            <div key={evento.id} className="bg-gray-100 p-4 rounded shadow flex flex-col gap-2 text-black oscuroBox">
                                <p className="font-semibold">{evento.nombre}</p>
                                <p>{evento.localizacion}</p>
                                <p>{evento.categoria}</p>
                                <p>{evento.precio} €</p>
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
