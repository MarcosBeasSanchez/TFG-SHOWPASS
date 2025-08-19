import { BrowserRouter, Routes, Route, Link, Form } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import VentanaPrincipal from "./pages/VentanaPrincipal";
import { useEffect, useState } from "react";

export default function App() {

  // Estado para guardar el usuario logueado
  const [user, setUser] = useState(null);

  // Al montar el componente, intenta recuperar el usuario guardado en localStorage
  useEffect(() => {
    const savedUser = localStorage.getItem("user");
    // Solo parsea si el valor es válido
    if (savedUser && savedUser !== "undefined" && savedUser !== "null") {
      try {
          const parsed = JSON.parse(savedUser);
      console.log("👤 Usuario cargado desde localStorage:", parsed); // 👈 Añade esto
      setUser(parsed);
      } catch (e) {
        setUser(null); // Si hay error, limpia el estado y localStorage
        localStorage.removeItem("user");
      }
    }
  }, []);

  // Función para cerrar sesión: elimina datos y redirige
  const handleLogout = () => {
    localStorage.removeItem("token"); // Elimina el token
    localStorage.removeItem("user"); // Elimina el usuario
    setUser(null);
    window.location.href = "/";
  };

  // Renderizado principal con rutas y navegación
  return (
    <BrowserRouter>

      <nav className="p-4 bg-gray-800 text-white flex gap-4 justify-between items-center">
  {!user ? (
    <>
      <Link to="/">
        <img
          src="https://img.icons8.com/ios-filled/50/ffffff/home.png"
          alt="Inicio"
          className="w-6 h-6"
        />
      </Link>
      <div className="flex gap-4">
        <Link to="/login" className="hover:underline">Login</Link>
        <Link to="/register" className="hover:underline">Registro</Link>
      </div>
    </>
  ) : (
    <div className="flex gap-4 items-center w-full justify-between">
      <span className="text-white">👋 Hola, {user?.nombre}</span>
      <button
        onClick={handleLogout}
        className="bg-red-500 px-3 py-1 rounded hover:bg-red-600"
      >
        Logout
      </button>
    </div>
  )}


</nav>

      {/* Definición de rutas */}
      <Routes>
        <Route path="/" element={<VentanaPrincipal />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
    </BrowserRouter>
  );
}

