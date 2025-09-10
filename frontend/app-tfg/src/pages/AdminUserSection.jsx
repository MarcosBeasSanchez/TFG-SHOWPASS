import React, { useState } from "react";
import config from "../config/config";

const AdminUserSection = () => {
  const [email, setEmail] = useState("");
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  // Buscar usuario por email
  const buscarUsuario = async () => {
    if (!email) return;
    setLoading(true);
    setMessage("");
    setUsuario(null);

    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/findByEmail?email=${encodeURIComponent(email)}`);
      if (res.status === 404) {
        setMessage("Usuario no encontrado");
        return;
      }
      if (!res.ok) throw new Error("Error al buscar usuario");
      const data = await res.json();
      setUsuario(data);
    } catch (err) {
      console.error(err);
      setMessage("❌ Error al buscar usuario");
    } finally {
      setLoading(false);
    }
  };

  // Reportar usuario usando el email ingresado
  const reportarUsuario = async () => {
    if (!email) return;
    setLoading(true);
    setMessage("");

    try {
      const res = await fetch(
        `${config.apiBaseUrl}/tfg/usuario/reportar?email=${encodeURIComponent(email)}`,
        {
          method: "PUT",
        }
      );
      if (res.status === 404) {
        setMessage("Usuario no encontrado para reportar");
        return;
      }
      if (!res.ok) throw new Error("Error al reportar usuario");
      const data = await res.json();
      setUsuario(data);
      setMessage("✅ Usuario reportado correctamente");
    } catch (err) {
      console.error(err);
      setMessage("❌ Error al reportar usuario");
    } finally {
      setLoading(false);
    }
  };

  // Eliminar usuario por ID
  const eliminarUsuario = async () => {
    if (!usuario) return;
    setLoading(true);
    setMessage("");

    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/delete/${usuario.id}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error("Error al eliminar usuario");
      setMessage("✅ Usuario eliminado correctamente");
      setUsuario(null);
      setEmail("");
    } catch (err) {
      console.error(err);
      setMessage("❌ Error al eliminar usuario");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mt-4 flex flex-col gap-4 max-w-md mx-auto">
      <p className="text-gray-500 mb-2 oscuroTextoGris">Buscar Usuario por Email:</p>
      <div className="flex flex-col sm:flex-row gap-2">
        <input
          type="text"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Correo del usuario..."
          className="flex-1 p-2 border rounded-sm focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500 oscuroBox"
        />
        <button
          onClick={buscarUsuario}
          className="w-full sm:w-auto bg-blue-500 text-white p-2 rounded hover:bg-blue-700 transition"
        >
          Buscar
        </button>
      </div>

      {loading && <p className="text-gray-600 mt-2">Cargando...</p>}
      {message && <p className="text-gray-700 mt-2">{message}</p>}

      {usuario && (
        <div
          className={`mt-4 p-4 border rounded flex flex-col gap-2 bg-gray-50 oscuroBox  ${usuario.reportado ? "border-red-500" : "border-gray-300"
            }`}
        >
          <p className="text-black oscuroTextoGris">
            <strong>ID:</strong> {usuario.id}
          </p>
          <p className="text-black oscuroTextoGris">
            <strong>Email:</strong> {usuario.email}
          </p>
          <p className="text-black oscuroTextoGris">
            <strong>Nombre:</strong> {usuario.nombre}
          </p>
          <p className="text-black oscuroTextoGris">
            <strong>Reportado:</strong> {usuario.reportado ? "Sí" : "No"}
          </p>

          <div className="flex flex-col sm:flex-row gap-2 mt-2">
            <button
              onClick={reportarUsuario}
              className="flex-1 bg-gray-500 text-white p-2 rounded hover:bg-gray-600 transition"
            >
              Reportar usuario
            </button>
            <button
              onClick={eliminarUsuario}
              className="flex-1 bg-red-500 text-white p-2 rounded hover:bg-red-600 transition"
            >
              Eliminar usuario
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminUserSection;