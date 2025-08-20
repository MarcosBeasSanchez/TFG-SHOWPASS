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
        console.log("👤 Usuario cargado desde localStorage:", parsed); 
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
      <nav className="p-4 bg-blue-950 text-white flex gap-4 justify-between items-center">
        <Link to="/">  {/* LINK A HOME */}
          <span
            className="material-symbols-outlined"
            style={{ fontSize: "30px", lineHeight: "1" }}
          >house</span>
        </Link>
        <Link to="/">
          <div className="flex items-center gap-2"> {/* LINK A HOME */}
            <h1 className="text-2xl font-extrabold"
              style={{ fontSize: "30px", lineHeight: "1", fontFamily: "Roboto, sans-serif" }}>
              SHOWPASS
            </h1>
            <span className="material-symbols-outlined"
              style={{ fontSize: "30px", lineHeight: "1" }}>
              local_activity
            </span>
          </div>
        </Link>
        <div className="flex gap-4 items-center relative">
          {!user ? (
            <>
              <ProfileDropdown>
                <Link to="/login" className="block px-4 py-2 hover:bg-gray-700">Login</Link>
                <Link to="/register" className="block px-4 py-2 hover:bg-gray-700">Registro</Link>
              </ProfileDropdown>
            </>
          ) : (
            <ProfileDropdown>
              <span className="block px-4 py-2 text-white">👋 Hola, {user?.nombre}</span>

              <button
                onClick={handleLogout}
                className="block w-full text-left px-4 py-2 hover:bg-gray-700"
              >
                Editar perfil
              </button>

              <button
                onClick={handleLogout}
                className="block w-full text-left px-4 py-2 bg-red-500 hover:bg-red-600"
              >
                Logout
              </button>
            </ProfileDropdown>
          )}
        </div>
      </nav>

      {/* Definición de rutas */}
      <Routes>
        <Route path="/" element={<VentanaPrincipal />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
    </BrowserRouter>
  );

  // Componente para el icono de perfil y el desplegable
  function ProfileDropdown({ children }) {
    const [open, setOpen] = useState(false);

    // Cierra el menú si se hace click fuera
    useEffect(() => {
      function handleClick(e) {
        if (!e.target.closest(".profile-dropdown")) setOpen(false);
      }
      if (open) document.addEventListener("mousedown", handleClick);
      return () => document.removeEventListener("mousedown", handleClick);
    }, [open]);

    return (
      <div className="profile-dropdown relative">
        <button
          onClick={() => setOpen((o) => !o)}
          className=" flex items-center justify-center"
        >
          <span
            className="material-symbols-outlined"
            style={{ fontSize: "30px", lineHeight: "1" }}
          >person</span>
        </button>
        {open && (
          <div className="absolute right-0 mt-2 w-40 bg-gray-800 rounded shadow-lg z-10">
            {children}
          </div>
        )}
      </div>
    );
  }
}

