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

  const crearEvento = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      const payload = new FormData();
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
        body: payload
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
        invitados: []
      });

      fetchEventos();
    } catch (err) {
      console.error(err);
      setMessage("❌ Error al crear evento");
    }
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
    <div className="mt-4 px-2 md:px-0 text-black">
      <h2 className="text-2xl font-bold mb-4">Administrar Eventos</h2>

      {/* Formulario para crear evento */}
      <form onSubmit={crearEvento} className="bg-gray-50 p-4 rounded shadow mb-4 flex flex-col gap-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 items-center">
          <label>Nombre:</label>
          <input type="text" name="nombre" value={formData.nombre} onChange={handleChange} required className="p-2 border rounded text-black"/>

          <label>Localización:</label>
          <input type="text" name="localizacion" value={formData.localizacion} onChange={handleChange} required className="p-2 border rounded text-black"/>

          <label>Inicio Evento:</label>
          <input type="datetime-local" name="inicioEvento" value={formData.inicioEvento} onChange={handleChange} required className="p-2 border rounded text-black"/>

          <label>Fin Evento:</label>
          <input type="datetime-local" name="finEvento" value={formData.finEvento} onChange={handleChange} required className="p-2 border rounded text-black"/>

          <label>Precio:</label>
          <input type="number" name="precio" value={formData.precio} onChange={handleChange} className="p-2 border rounded text-black"/>

          <label>Categoría:</label>
          <select name="categoria" value={formData.categoria} onChange={handleChange} required className="p-2 border rounded text-black">
            <option value="">Selecciona categoría</option>
            <option value="MUSICA">Música</option>
            <option value="DEPORTES">Deportes</option>
            <option value="ARTE">Arte</option>
            <option value="VIDEOJUEGOS">Videojuegos</option>
            <option value="OTROS">Otros</option>
          </select>

          <label>Imagen principal:</label>
          <input type="file" name="imagen" onChange={handleChange} className="p-2 border rounded text-black"/>

          <label>Descripción:</label>
          <textarea name="descripcion" value={formData.descripcion} onChange={handleChange} className="p-2 border rounded text-black md:col-span-2"/>

          <label>Carrusel de imágenes:</label>
          <input type="file" multiple onChange={handleCarruselChange} className="p-2 border rounded text-black md:col-span-2"/>
          <div className="flex flex-wrap gap-2 md:col-span-2">
            {formData.carrusels.map((file, idx) => (
              <div key={idx} className="relative w-24 h-24">
                <img src={URL.createObjectURL(file)} alt={`Carrusel ${idx}`} className="w-full h-full object-cover rounded border"/>
                <button type="button" onClick={() => removerImagenCarrusel(idx)} className="absolute top-0 right-0 bg-red-500 text-white rounded-full w-6 h-6">×</button>
              </div>
            ))}
          </div>
        </div>

        {/* Invitados */}
        <div className="flex flex-col gap-4 mt-4">
          <h3 className="font-bold">Invitados</h3>
          {formData.invitados.map((invitado, idx) => (
            <div key={idx} className="bg-gray-200 p-4 rounded shadow grid grid-cols-1 md:grid-cols-4 gap-2 items-center">
              <input type="text" name="nombre" placeholder="Nombre" value={invitado.nombre} onChange={(e) => handleInvitadoChange(idx, e)} className="p-2 border rounded text-black"/>
              <input type="text" name="apellidos" placeholder="Apellidos" value={invitado.apellidos} onChange={(e) => handleInvitadoChange(idx, e)} className="p-2 border rounded text-black"/>
              <input type="file" name="fotoURL" onChange={(e) => handleInvitadoChange(idx, e)} className="p-2 border rounded text-black"/>
              <button type="button" onClick={() => removerInvitado(idx)} className="bg-red-500 text-white p-2 rounded hover:bg-red-600 transition">Quitar</button>
            </div>
          ))}
          <button type="button" onClick={agregarInvitado} className="bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition w-40">Agregar invitado</button>
        </div>

        <button type="submit" className="bg-green-500 text-white p-2 rounded hover:bg-green-600 transition mt-4 w-full md:w-40">Crear evento</button>
      </form>

      {message && <p className="text-center mb-4 text-black">{message}</p>}

      {/* Lista de eventos */}
      {loading ? (
        <p className="text-black text-center">Cargando eventos...</p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {eventos.map((evento) => (
            <div key={evento.id} className="bg-gray-100 p-4 rounded shadow flex flex-col gap-2 text-black">
              <p className="font-semibold">{evento.nombre}</p>
              <p>{evento.localizacion}</p>
              <p>{evento.categoria}</p>
              <p>{evento.precio} €</p>
              <button onClick={() => eliminarEvento(evento.id)} className="bg-red-500 text-white p-2 rounded hover:bg-red-600 transition mt-2">Eliminar</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AdminEvents;
