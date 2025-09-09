import React, { useEffect, useState } from "react";
import config from "../config/config";

const AdminReportSection = () => {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchReportados = async () => {
      try {
        const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/findAllReportados`);
        if (!res.ok) throw new Error("Error al obtener usuarios reportados");
        const data = await res.json();
        setUsuarios(data);
      } catch (err) {
        console.error("❌ Error cargando usuarios reportados:", err);
        setUsuarios([]);
      } finally {
        setLoading(false);
      }
    };
    fetchReportados();
  }, []);

  if (loading) return <p className="text-center mt-10">Cargando usuarios reportados...</p>;
  if (usuarios.length === 0) return <p className="text-center mt-10">No hay usuarios reportados.</p>;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-4">
      {usuarios.map((usuario) => (
        <div
          key={usuario.id}
          className={`p-4 rounded shadow flex flex-col gap-2 bg-gray-50 border ${
            usuario.reportado ? "border-red-500" : "border-gray-300"
          }`}
        >
          <div className="flex justify-between items-center">
            <p className="text-black font-semibold">{usuario.nombre}</p>
            {usuario.reportado && (
              <span className="text-white bg-red-500 text-xs px-2 py-1 rounded">
                Reportado
              </span>
            )}
          </div>
          <p className="text-black">{usuario.email}</p>
        </div>
      ))}
    </div>
  );
};

export default AdminReportSection;