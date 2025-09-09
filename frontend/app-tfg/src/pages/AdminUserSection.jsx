import React from "react";

const AdminUserSection = () => (
  <div className="mt-4 flex flex-col gap-2">
    <div className='flex flex-col gap-2'>
      <p className="text-gray-500 mb-2">Buscar Usuario por Email:</p>
      <form action="" method="get" className="flex justify-center">
        <input
          type="text"
          placeholder="Buscar usuario..."
          className="w-full p-2 border rounded-sm focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500"
        />
      </form>
      <div className="mt-2 flex flex-row gap-2 justify-center">
        <button className="w-full bg-gray-500 text-white p-2 rounded hover:bg-gray-600 transition">Reportar usuario</button>
        <button className="w-full bg-red-500 text-white p-2 rounded hover:bg-red-600 transition">Eliminar usuario</button>
      </div>
    </div>
  </div>
);

export default AdminUserSection;