export default function VentanaPrincipal() {
  const entradas = [
    {
      id: 1,
      nombre: "Concierto",
      descripcion: "Vive la mejor música en directo",
      imagen: "https://tse2.mm.bing.net/th/id/OIP.iCgsEs81VE8Wpx7OsqID3gHaFj?pid=Api&P=0&h=180",
    },
    {
      id: 2,
      nombre: "Teatro",
      descripcion: "Disfruta de las mejores obras",
      imagen: "https://tse1.mm.bing.net/th/id/OIP.yDXYxSaYQGcpohaFbHWh9AHaFX?pid=Api&P=0&h=180",
    },
    {
      id: 3,
      nombre: "Deportes",
      descripcion: "Vibra con tus equipos favoritos",
      imagen: "https://tse3.mm.bing.net/th/id/OIP.ykJzlxLYJqLqiLDaZr5ODAHaFq?pid=Api&P=0&h=180",
    },
  ];

  return (
    <div className="p-8 grid grid-cols-1 md:grid-cols-3 gap-6">
      {entradas.map((entrada) => (
        <div
          key={entrada.id}
          className="bg-white rounded-2xl shadow-lg overflow-hidden hover:scale-105 transition"
        >
          <img src={entrada.imagen} alt={entrada.nombre} className="w-full h-48 object-cover" />
          <div className="p-4">
            <h3 className="text-xl font-bold">{entrada.nombre}</h3>
            <p className="text-gray-600">{entrada.descripcion}</p>
            <button className="mt-3 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600">
              Comprar
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}