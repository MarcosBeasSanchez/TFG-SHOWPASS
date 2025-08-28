import { useEffect, useState } from "react";
import config from "../config/config";

export default function ShoppingCart({ user }) {
  const [carrito, setCarrito] = useState(null);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  //cogemos el id del usuario desde localStorage
  const usuarioId = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).id : null;

  useEffect(() => {
    if (!usuarioId) return;

    const fetchCarrito = async () => {
      try {
        setLoading(true);
        const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/${usuarioId}`);
        if (!res.ok) throw new Error("No se pudo cargar el carrito");
        console.log("Carrito:", res);
        const data = await res.json();
        setCarrito(data);

        // Calcular total
        const totalRes = await fetch(`${config.apiBaseUrl}/tfg/carrito/total/${usuarioId}`);
        const totalData = await totalRes.json();
        setTotal(totalData);
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCarrito();
  }, [usuarioId]);

  const eliminarEvento = async (eventoId) => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/eliminar/${usuarioId}/${eventoId}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error("Error eliminando evento");
      const data = await res.json();
      setCarrito(data);

      // Actualizar total
      const totalRes = await fetch(`${config.apiBaseUrl}/tfg/carrito/total/${usuarioId}`);
      const totalData = await totalRes.json();
      setTotal(totalData);
    } catch (err) {
      console.error(err);
      alert("No se pudo eliminar el evento");
    }
  };

  const vaciarCarrito = async () => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/vaciar/${usuarioId}`, 
        { method: "DELETE" });
      if (!res.ok) throw new Error("Error vaciando carrito");
      const data = await res.json();
      setCarrito(data);
      setTotal(0);
    } catch (err) {
      console.error(err);
      alert("No se pudo vaciar el carrito");
    }
  };

  const finalizarCompra = async () => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/carrito/finalizar/${usuarioId}`, { method: "POST" });
      if (!res.ok) throw new Error("Error al finalizar la compra");
      const data = await res.json();
      setCarrito(data);
      setTotal(0);
      alert("Compra realizada con éxito!");
    } catch (err) {
      console.error(err);
      alert("No se pudo finalizar la compra");
    }
  };

  {/*if (loading) return <p className="p-4">Cargando carrito...</p>;*/ }
  if (error) return <p className="p-4 text-red-500">Error: {error}</p>;
  if (!carrito || carrito.eventos.length === 0)
    return (
      <div className="flex flex-col items-center justify-center min-h-screen w-full px-4">
  <div className="bg-white shadow-lg rounded-lg p-6 sm:p-8 flex flex-col items-center w-full max-w-md">
    <span className="material-symbols-outlined text-5xl sm:text-6xl text-gray-400 mb-4">
      shopping_cart_off
    </span>
    <h2 className="text-xl sm:text-2xl font-bold text-gray-700 mb-2 text-center">Tu carrito está vacío</h2>
    <p className="text-gray-500 mb-4 text-center">¡Añade eventos para empezar tu compra!</p>
    <img
      src="https://i.giphy.com/giXLnhxp60zEEIkq8K.webp"
      alt="vacio..."
      className="w-40 h-40 sm:w-60 sm:h-60 mb-6 object-contain"
    />
    <a
      href="/"
      className="bg-blue-500 text-white px-4 sm:px-6 py-2 rounded-lg hover:bg-blue-600 transition text-center w-full"
    >
      Ir a eventos
    </a>
  </div>
</div>
    );

  return (
    <div className="max-w-5xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-blue-950">Tu Carrito de Compras</h1>

      <div className="space-y-4">
        {carrito.eventos.map((evento, index) => (
          <div key={`${evento.id}-${index}`} className="flex items-center justify-between pr-5 bg-white shadow border-gray-500 border-l-4 p-2 ">
            <div className="flex items-center gap-4">
              <img src={evento.imagen} alt={evento.nombre} className="w-30 h-30 object-cover" />
              <div>
                <h2 className="text-xl font-semibold text-blue-950">{evento.nombre}</h2>
                <p className="text-gray-600">{evento.localizacion}</p>
                <p className="text-gray-500">{evento.precio.toFixed(2)} €</p>
              </div>
            </div>
            <button
              onClick={() => eliminarEvento(evento.id)}
              className="bg-red-500 text-white px-4 py-2 text-xs rounded hover:bg-red-600"
            >
              Eliminar
            </button>
          </div>
        ))}
      </div>

      <div className="mt-6 flex justify-between items-center">
        <p className="text-xl font-bold text-blue-950 ">Total: ${total.toFixed(2)}</p>
        <div className="flex gap-4">
          <button
            onClick={vaciarCarrito}
            className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
          >
            Vaciar carrito
          </button>
          <button
            onClick={finalizarCompra}
            className="bg-green-500 text-white px-6 py-2 rounded hover:bg-green-600"
          >
            Comprar
          </button>
        </div>
      </div>
    </div>
  );
}