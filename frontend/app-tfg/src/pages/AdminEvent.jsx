
const AdminEvents = () => {
    return (
        <div className="mt-4 flex flex-row gap-2 justify-center">
            <button className="w-full bg-green-500 text-white p-2 rounded hover:bg-green-600 transition">Crear evento</button>
            <button className="w-full bg-red-500 text-white p-2 rounded hover:bg-red-600 transition">Eliminar evento</button>
        </div>
    );
}
export default AdminEvents;