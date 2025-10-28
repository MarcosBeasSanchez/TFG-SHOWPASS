  import React, { useState, useEffect, useCallback } from "react";
  import config from "../config/config";

  const AdminUserSection = () => {
    const [email, setEmail] = useState("");
    const [usuario, setUsuario] = useState(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    // Reportados
    const [usuariosReportados, setUsuarios] = useState([]);

    // Callback para obtener usuarios reportados
    // Esto hace que la función sea accesible en todo el componente y que React la optimice.
    const fetchReportados = useCallback(async () => {
      setLoading(true);
      try {
        const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/findAllReportados`);
        if (!res.ok) throw new Error("Error al obtener usuarios reportados");
        const data = await res.json();
        setUsuarios(data);
        setMessage(""); // Limpia mensajes si la carga es exitosa
      } catch (err) {
        console.error("❌ Error cargando usuarios reportados:", err);
        setUsuarios([]);
        setMessage("❌ Error cargando usuarios reportados."); // Muestra el error en la UI
      } finally {
        setLoading(false);
      }
    }, []); 

    // useEffect PARA LLAMAR A LA FUNCIÓN AL MONTARSE
    useEffect(() => {
      fetchReportados();
    }, [fetchReportados]); // Dependencia: fetchReportados (debe estar en useCallback)

    // Buscar usuario por email
    const buscarUsuario = async () => {
      if (!email) {
        setMessage("Por favor, introduce un email.");
        return;
      }
      setLoading(true);
      setMessage("");
      setUsuario(null);

      try {
        const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/findByEmail?email=${encodeURIComponent(email)}`);
        if (res.status === 404) {
          setMessage("❌ Usuario no encontrado");
          return;
        }
        if (!res.ok) throw new Error(`Error al buscar usuario: ${res.status}`);
        const data = await res.json();
        setUsuario(data);
        setMessage(`✅ Usuario:  ${data.email} encontrado.`);
      } catch (err) {
        console.error(err);
        setMessage("❌ Error al buscar usuario.");
      } finally {
        setLoading(false);
      }
    };

    // Reportar usuario usando el email ingresado
    const reportarUsuario = async () => {
      // Si el usuario ya está cargado, usamos su email, si no, usamos el input
      const targetEmail = usuario ? usuario.email : email;

      if (!targetEmail) return;
      setLoading(true);
      setMessage("");
      try {
        const res = await fetch(
          `${config.apiBaseUrl}/tfg/usuario/reportar?email=${encodeURIComponent(targetEmail)}`,
          {
            method: "PUT",
          }
        );
        if (res.status === 404) {
          setMessage("Usuario no encontrado para reportar");
          return;
        }
        if (!res.ok) throw new Error(`Error al reportar usuario: ${res.status}`);

        const data = await res.json();
        setUsuario(data); // Actualiza el estado del usuario encontrado

        // Intentar refrescar la lista de reportados (Si es necesario)
        fetchReportados(); 
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    // Eliminar usuario por ID
    const eliminarUsuario = async () => {
      if (!usuario || !usuario.id) {
        setMessage("Por favor, busca un usuario primero.");
        return;
      }
      setLoading(true);
      setMessage("");

      try {
        const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/delete/${usuario.id}`, {
          method: "DELETE",
        });
        if (!res.ok) throw new Error(`Error al eliminar usuario: ${res.status}`);

        setMessage(`✅ Usuario con ID ${usuario.id} eliminado correctamente`);
        setUsuario(null);
        setEmail("");
        // Refrescar la lista de reportados
         fetchReportados(); 

      } catch (err) {
        console.error(err);
        setMessage("❌ Error al eliminar usuario");
      } finally {
        setLoading(false);
      }
    };

    // El renderizado de la lista de usuarios debe manejar el estado de carga
    const hasReportedUsers = usuariosReportados && usuariosReportados.length > 0;

    return (
      <div className="mt-4 flex flex-col gap-4 w-full mx-auto">
        <h3 className="text-xl text-gray-500 font-bold oscuroTextoGris">Administración de Usuarios</h3>

        {/* SECCIÓN DE BÚSQUEDA Y ACCIONES */}
        <div className="p-4 shadow-md rounded   ">
          <p className="text-gray-500 mb-2 oscuroTextoGris">Buscar Usuario por Email:</p>
          <div className="flex flex-col sm:flex-row justify-center gap-2">
            <input
              type="text"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Correo del usuario..."
              className=" w-full p-2 border rounded-sm focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500 oscuroBox"
            />
            <button
              onClick={buscarUsuario}
              className="w-full sm:w-auto bg-blue-500 text-white p-2 rounded hover:bg-blue-700 transition"
              disabled={loading} // Deshabilita mientras carga
            >
              {loading ? "Buscando..." : "Buscar"}
            </button>
          </div>

          {/* Mensajes de estado */}
          {loading && <p className="text-gray-500 mt-2">Cargando...</p>}
          {message && (
            <p className={`my-5  ${message.startsWith('❌') ? 'text-red-600' : 'text-green-600'}`}>
              {message}
            </p>
          )}

          {/* DETALLES DEL USUARIO ENCONTRADO */}
          {usuario && (
            <div
              className={`mt-4 p-4 border rounded flex flex-col gap-2 bg-gray-50 oscuroBox ${usuario.reportado ? "border-red-500" : "border-gray-300"
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
                  onClick={reportarUsuario} // <-- Se usa la misma función que gestiona el toggle en el backend
                  className={`flex-1 p-2 rounded transition 
                    ${usuario.reportado
                      ? "bg-orange-500 text-white hover:bg-orange-600" // Color si está reportado (para des-reportar)
                      : "bg-gray-500 text-white hover:bg-gray-600" // Color si NO está reportado (para reportar)
                    }`}
                  disabled={loading}
                >
                  {/* El texto cambia según el estado actual */}
                  {usuario.reportado ? "Des-reportar usuario" : "Reportar usuario"}
                </button>

                <button
                  onClick={eliminarUsuario}
                  className="flex-1 bg-red-500 text-white p-2 rounded hover:bg-red-600 transition"
                  disabled={loading}
                >
                  Eliminar usuario
                </button>
              </div>

            </div>
          )}
        </div>

        {/* LISTADO DE USUARIOS REPORTADOS */}
        <h3 className="text-xl text-gray-500 font-bold oscuroTextoGris">Usuarios Reportados ({usuariosReportados.length})</h3>

        {!loading && !hasReportedUsers && <p className="text-center mt-4">No hay usuarios reportados.</p>}

        {hasReportedUsers && (
          <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-4">
            {usuariosReportados.map((usuario) => (
              <div
                key={usuario.id}
                className={`p-4 rounded shadow flex flex-col gap-2 bg-gray-50 border oscuroBox ${usuario.reportado ? "border-red-500" : "border-gray-300"
                  }`}
              >
                <div className="flex justify-between items-center">
                  <p className="text-black font-semibold oscuroTextoGris">{usuario.nombre}</p>
                  {usuario.reportado && (
                    <span className="text-white bg-red-500 text-xs px-2 py-1 rounded">
                      Reportado
                    </span>
                  )}
                </div>
                <p className="text-black oscuroTextoGris">{usuario.email}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    );
  };

  export default AdminUserSection;