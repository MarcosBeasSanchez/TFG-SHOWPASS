import { BrowserRouter, Routes, Route, Link, Form } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import VentanaPrincipal from "./pages/VentanaPrincipal";
import Profile from "./pages/Profile";
import EventDetail from "./pages/EventDetail";
import About from "./pages/About";
import Contact from "./pages/Contact";
import ShoppingCart from "./pages/ShoppingCart";

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
      <div className="flex flex-col min-h-screen">
        <nav className="pl-8 pr-8 pt-4 pb-4 bg-blue-950 text-white flex items-center justify-between">
          {/* Bloque Izquierda: Home + Titulo */}
          <div className="flex items-center gap-4">
            <Link to="/">
              <div className="flex items-center gap-2">
                <h1 className="text-2xl font-extrabold hover:scale-105 transition-transform duration-200"
                  style={{ fontSize: "30px", lineHeight: "1", fontFamily: "Roboto, sans-serif" }}>
                  SHOWPASS
                </h1>
                <span className="material-symbols-outlined"
                  style={{ fontSize: "30px", lineHeight: "1" }}>
                  local_activity
                </span>
              </div>
            </Link>
          </div>

          <div className="flex-1 flex justify-center">
          </div>
          <div className="flex gap-4 items-center relative">
            {!user ? (
              <>
                <Link to="/login" className="p-1 hover:bg-gray-900">Login</Link>
                <Link to="/register" className="p-1 hover:bg-gray-900">Registro</Link>
              </>
            ) : (
              <ProfileDropdown>
                <span className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 text-white">👋 Hola, {user?.nombre}</span>
                <Link
                  to="/profile"
                  className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                >
                  <span className="material-symbols-outlined pr-2 ">
                    manage_accounts
                  </span>
                  Editar perfil
                </Link>
                <Link
                  to="/shoppingCart"
                  className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                >
                  <span className="material-symbols-outlined pr-2 ">
                    shopping_cart
                  </span>
                  Ver carrito
                </Link>
                <Link
                  onClick={handleLogout}
                  className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 bg-red-500 hover:bg-red-600"
                >
                  <span className="material-symbols-outlined pr-2 ">
                    logout
                  </span>
                  Logout
                </Link>
              </ProfileDropdown>
            )}
          </div>
        </nav>

        {/* Definición de rutas */}
        <div className="flex-1">
          <Routes>
            <Route path="/" element={<VentanaPrincipal />} />
            <Route path="/about" element={<About />} />
            <Route path="/contact" element={<Contact />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<Profile setUser={setUser} />} />
            <Route path="/evento/:nombre" element={<EventDetail />} />
            <Route path="/shoppingCart" element={<ShoppingCart />} />
          </Routes>
        </div>

        {/* Footer */}
        <footer className="bg-black text-white text-center py-4 ">
          <div className="container mx-auto flex flex-col md:flex-row items-center justify-between px-4">
            <span className="text-sm">&copy; {new Date().getFullYear()} SHOWPASS Todos los derechos reservados.</span>
            <div className="flex gap-4 mt-2 md:mt-0">
              <a href="https://github.com/MarcosBeasSanchez/TFG" target="_blank" rel="noopener noreferrer" className="hover:underline">GitHub Repositorio</a>
              <a href="/about" className="hover:underline">Sobre nosotros</a>
              <a href="/contact" className="hover:underline">Contacto</a>
            </div>
          </div>
        </footer>
      </div>
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
          {user?.foto ? (
            <img src={user.foto}
              alt="Foto perfil"
              className="w-10 h-10 rounded-full object-cover border hover:scale-105 transition-transform duration-200 border-none"
            />
          ) : (
            <span
              className="material-symbols-outlined"
              style={{ fontSize: "30px", lineHeight: "1" }}
            >person</span>
          )}
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

