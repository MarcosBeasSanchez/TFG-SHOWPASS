// ----------------------------------------------------
// 1. IMPORTS
// ----------------------------------------------------
// Importaciones de React Router DOM para manejar la navegación en la aplicación de una sola página (SPA).
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
// Importación de componentes de páginas
import Login from "./pages/Login";
import Register from "./pages/Register";
import VentanaPrincipal from "./pages/VentanaPrincipal";
import Profile from "./pages/Profile";
import EventDetail from "./pages/EventDetail";
import About from "./pages/About";
import Contact from "./pages/Contact";
import ShoppingCart from "./pages/Carrito";
import CategoryEvents from "./pages/CategoryEvents";
import BusquedaEventos from "./pages/Busqueda";
import AdminPanel from "./pages/AdminPanel";
import VendedorPanel from "./pages/VendedorPanel";
import UserTickets from "./pages/Tickets.jsx";
// Importación de hooks de React para manejar el estado y efectos secundarios.
import { useEffect, useState } from "react";
// Importación de la configuración (URL base de la API, etc.)
import config from "./config/config";

// ----------------------------------------------------
// 2. COMPONENTE PRINCIPAL APP
// ----------------------------------------------------

export default function App() {
  // Estado para almacenar la información del usuario logueado (o null si no hay sesión).
  const [user, setUser] = useState(null);
  // Estado para controlar la visibilidad del menú lateral (Drawer) en dispositivos móviles.
  const [drawerOpen, setDrawerOpen] = useState(false);
  // Estado para controlar si la aplicación está validando la sesión al inicio.
  const [loadingAuth, setLoadingAuth] = useState(true); // Nuevo estado de carga de autenticación
  // Estado para el modo oscuro
  const [darkMode, setDarkMode] = useState(false);

// ----------------------------------------------------
// 3. EFECTO: CONTROL DE MODO OSCURO (DARK MODE)
// ----------------------------------------------------
  /* Aplica o quita la clase 'dark' al elemento HTML raíz (document.documentElement).
     Esto es común para usar frameworks CSS como Tailwind que controlan el modo oscuro con clases. */  
    useEffect(() => {
    const root = document.documentElement;
    if (darkMode) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }, [darkMode]); // Se re-ejecuta cada vez que 'darkMode' cambia.

// ----------------------------------------------------
// 4. EFECTO: LÓGICA DE AUTO-LOGIN Y PERSISTENCIA DE SESIÓN
// ----------------------------------------------------
/* Se ejecuta una vez al montar el componente. Intenta recuperar el token del almacenamiento local 
     y validarlo contra el backend para mantener la sesión activa. */
    useEffect(() => {
        const savedToken = localStorage.getItem("token");
        
        const checkAuth = async () => {
            setLoadingAuth(true); // Inicia el estado de carga

            if (savedToken) {
                try {
                    // 1. Llamada a la API para obtener el perfil (endpoint protegido)
                    const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/perfil`, {
                        method: 'GET',
                        headers: { 
                            // Adjunta el token JWT en el encabezado de Autorización (Bearer Token)
                            'Authorization': `Bearer ${savedToken}`, 
                            'Content-Type': 'application/json' 
                        }
                    });
                    
                    if (res.ok) {
                        // Caso A: Token válido (200 OK) - Establece el usuario en el estado
                        const data = await res.json();
                        setUser(data); // Establece los datos actualizados del perfil
                        localStorage.setItem("user", JSON.stringify(data)); // Opcional: Actualizar el localStorage con datos frescos
                        console.log("Auto-Login Exitoso. Sesión persistente activa.");
                    } else if (res.status === 401) {
                        // Caso B: Token inválido/expirado (401 Unauthorized) - Llama a la función de cierre de sesión
                        console.log("Token expirado o inválido. Forzando logout.");
                        handleLogout(); // Limpia el estado y el localStorage
                    } else {
                        // Otro error del servidor (ej. 500)
                        console.error("Error desconocido al validar sesión:", res.status);
                        handleLogout(); 
                    }
                } catch (e) {
                    // Error de red (ej. backend caído)
                    console.error("Error de red al intentar validar sesión:", e);
                }
            }
            setLoadingAuth(false); // Finaliza el estado de carga
        };

        checkAuth();
    }, []); // Se ejecuta SÓLO una vez al montar el componente App

// ----------------------------------------------------
// 5. RENDERIZADO CONDICIONAL DE CARGA
// ----------------------------------------------------
/* Muestra una pantalla de "Cargando" mientras se está validando la sesión (loadingAuth es true). */
    if (loadingAuth) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <p className="text-xl">Cargando sesión y datos iniciales...</p>
            </div>
        );
    }

// ----------------------------------------------------
// 6. FUNCIONES UTILITARIAS
// ----------------------------------------------------
// Función para cerrar la sesión (limpia el almacenamiento local y el estado de usuario).
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    window.location.href = "/";
  };
// Función que determina la fuente de la imagen (URL, Base64 prefijado, o Base64 sin prefijo).
  const getImageSrc = (img) => {
    if (!img) return null; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };

  // ----------------------------------------------------
  //  ESTRUCTURA Y RUTAS DE LA APLICACIÓN
  // ----------------------------------------------------

  return (
    // Componente principal para habilitar el enrutamiento de React Router.
    <BrowserRouter>
      <div className="flex flex-col min-h-screen ">
        {/* ----------------- BARRA DE NAVEGACIÓN (HEADER) ----------------- */}
        <nav className="pl-2 pr-2 sm:pl-8  pt-4 pb-4 bg-blue-950 text-white flex items-center justify-between gap-2 sm:pr-8 sm:gap-0">

          {/* Logo y enlace a la página principal */}
          <div className="flex items-center gap-4 ">
            <Link to="/">
            {/* ... Contenido del logo ... */}
              <div className="flex items-center gap-2 ml-2 sm:mb-0">
                <h1
                  className="text-2xl font-extrabold hover:scale-105 transition-transform duration-200"
                  style={{ fontSize: "30px", lineHeight: "1", fontFamily: "Roboto, sans-serif" }}
                >
                  SHOWPASS
                </h1>
                <span
                  className="material-symbols-outlined"
                  style={{ fontSize: "30px", lineHeight: "1" }}
                >
                  local_activity
                </span>
              </div>
            </Link>
          </div>

          {/* Botón menú lateral en móvil */}
          <div className="flex items-center gap-2 ">
            <button
              className="sm:hidden px-2 py-2 rounded-full hover:bg-blue-800"
              onClick={() => setDarkMode((prev) => !prev)}>
              <span className="material-symbols-outlined align-middle " style={{ fontSize: "24px" }}>
                dark_mode
              </span>
            </button>
            <button
              className="sm:hidden flex items-center px-2 py-2 rounded-full hover:bg-blue-800"
              onClick={() => setDrawerOpen(true)}
            >
              <span className="material-symbols-outlined align-middle" style={{ fontSize: "32px" }}>menu</span>
            </button>
          </div>

          {/* Enlaces de categorías (solo en escritorio) */}
          <div className="hidden sm:flex w-auto justify-center flex-1">
            <div className="flex gap-6 overflow-x-auto scrollbar-hide [@media(max-width:800px)]:hidden">
              {["MUSICA", "DEPORTES", "ARTE", "VIDEOJUEGOS", "OTROS"].map((cat) => (
                <Link
                  key={cat}
                  to={`/categoria/${cat}`}
                  className="text-sm px-1 py-1 hover:bg-blue-800 rounded whitespace-nowrap"
                >
                  {cat}
                </Link>
              ))}
            </div>
          </div>

          {/* Bloque de usuario: Login/Registro o Perfil/Logout (solo en escritorio) */}
          <div className="hidden sm:flex gap-4 items-center relative ">
            {!user ? ( // Si no hay usuario logueado
              <>
                <Link to="/login" className=" text-sm font-medium p-1  hover:bg-blue-800 rounded" >LOGIN</Link>
                <Link to="/register" className=" text-sm font-medium p-1  hover:bg-blue-800 rounded " >REGISTRO</Link>                
                <span
                  className="material-symbols-outlined hover:bg-blue-800 rounded-full p-1 transition-colors duration-300  cursor-pointer"
                  title={darkMode ? "Desactivar modo oscuro" : "Activar modo oscuro"}
                  onClick={() => setDarkMode((prev) => !prev)}
                >
                  dark_mode
                </span>
              </>
            ) : ( // Si hay usuario logueado
              <div className="flex items-center gap-4">
                {/* Botón de Modo Oscuro */}
                <span
                  className="material-symbols-outlined hover:bg-blue-800 rounded-full p-1 transition-colors cursor-pointer"
                  title={darkMode ? "Desactivar modo oscuro" : "Activar modo oscuro"}
                  onClick={() => setDarkMode((prev) => !prev)}
                >
                  {darkMode ? "light_mode" : "dark_mode"}
                </span>

                {/* Contenido del menú desplegable del perfil (perfil, carrito, tickets, paneles admin/vendedor, logout) */}
                {/* ...Enlaces condicionales por rol (ADMIN, VENDEDOR) y Logout... */}
                <ProfileDropdown>
                  <span className="flex w-full text-left text-sm items-center justify-items-center-safe px-2 py-2 text-white">
                    👋 Hola, {user?.nombre}
                  </span>

                  {/* Enlaces del menú desplegable del perfil */}
                  <Link
                    to="/profile"
                    className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                  >
                    <span className="material-symbols-outlined pr-2 ">
                      manage_accounts
                    </span>
                    Editar perfil
                  </Link>
                  {/* Enlace al carrito de compras */}
                  <Link
                    to="/shoppingCart"
                    className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                  >
                    <span className="material-symbols-outlined pr-2 ">
                      shopping_cart
                    </span>
                    Ver carrito
                  </Link>
                  {/* Enlace a los tickets */}
                  <Link
                    to="/tickets"
                    className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                  >
                    <span className="material-symbols-outlined pr-2 ">
                      qr_code
                    </span>
                    Ver tickets
                  </Link>

                  {/* Enlaces condicionales por rol (ADMIN, VENDEDOR) */}
                  {user?.rol === "ADMIN" && (
                    <Link
                      to="/admin"
                      className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                    >
                      <span className="material-symbols-outlined pr-2">
                        admin_panel_settings
                      </span>
                      Panel Admin
                    </Link>
                  )}

                  {user?.rol === "VENDEDOR" && (
                    <Link
                      to="/vendedor"
                      className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                    >
                      <span className="material-symbols-outlined pr-2">
                        add
                      </span>
                      Panel Vendedor
                    </Link>
                  )}

                  {/* Botón de Logout */}
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
              </div>
            )}
          </div>
        </nav>

        {/* ----------------- MENÚ LATERAL (DRAWER) PARA MÓVIL ----------------- */}
        {drawerOpen && (
          <div className="fixed inset-0 z-50 flex">
            {/* Fondo oscuro */}
            <div
              className="fixed inset-0 bg-black opacity-50"
              onClick={() => setDrawerOpen(false)}
            />
            {/* Panel lateral */}
            <div className="relative bg-blue-950 text-white w-64 max-w-full h-full shadow-lg flex flex-col p-6">
              <button
                className="absolute top-4 right-4 text-white"
                onClick={() => setDrawerOpen(false)}
              >
                <span className="material-symbols-outlined" style={{ fontSize: "32px" }}>close</span>
              </button>
              <div className="flex flex-col gap-4 mt-8">
                {["MUSICA", "DEPORTES", "ARTE", "VIDEOJUEGOS", "OTROS"].map((cat) => (
                  <Link
                    key={cat}
                    to={`/categoria/${cat}`}
                    className="text-base px-2 py-2 hover:bg-blue-800 rounded"
                    onClick={() => setDrawerOpen(false)}
                  >
                    {cat}
                  </Link>
                ))}
                <hr className="my-4 border-blue-800" />
                {!user ? ( // Si no hay usuario logueado
                  <>
                    <Link to="/login" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>LOGIN</Link>
                    <Link to="/register" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>REGISTRO</Link>
                  </>
                ) : ( // Si hay usuario logueado
                  <>
                    {/* Enlaces del menú lateral del perfil */}
                    <Link to="/profile" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>
                      <span className="material-symbols-outlined pr-2">manage_accounts</span>
                      Editar perfil
                    </Link>
                    {/* Enlace al carrito de compras */}
                    <Link to="/shoppingCart" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>
                      <span className="material-symbols-outlined pr-2 align-middle">shopping_cart</span>
                      Ver carrito
                    </Link>
                    {/* Enlace a los tickets */}
                    <Link
                      to="/tickets"
                      className="text-base px-2 py-2 hover:bg-blue-800 rounded **flex items-center**"
                      onClick={() => setDrawerOpen(false)}
                    >
                      <span className="material-symbols-outlined pr-2 align-middle ">qr_code</span>
                      Ver tickets
                    </Link>
                    {/* Enlaces condicionales por rol (ADMIN, VENDEDOR) */}
                    {user?.rol === "ADMIN" && (
                      <Link
                        to="/admin"
                        className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-blue-800 rounded"
                      >
                        <span className="material-symbols-outlined pr-2">
                          admin_panel_settings
                        </span>
                        Panel Admin
                      </Link>
                    )}
                    {user?.rol === "VENDEDOR" && (
                      <Link
                        to="/vendedor"
                        className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-blue-800 rounded"
                      >
                        <span className="material-symbols-outlined pr-2">
                          add
                        </span>
                        Panel Vendedor
                      </Link>
                    )}
                    
                    {/* Botón para cambiar entre modo claro y modo oscuro */}
                    <button className="flex items-center  px-2 py-2 gap-2 hover:bg-blue-800 rounded" onClick={() => setDarkMode((prev) => !prev)}>
                      <span className="material-symbols-outlined ">
                        dark_mode
                      </span> {darkMode ? "Modo claro" : "Modo oscuro"}
                    </button>

                    {/* Botón de Logout */}
                    <button
                      onClick={() => { handleLogout(); setDrawerOpen(false); }}
                      className="text-base px-2 py-2 bg-red-500 hover:bg-red-600 rounded flex items-center"
                    >
                      <span className="material-symbols-outlined pr-2">logout</span>
                      Logout
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ----------------- CONTENIDO PRINCIPAL Y RUTAS ----------------- */}
        <div className="flex-1">
          {/* Define todas las rutas de la aplicación */}
          <Routes>
            <Route path="/" element={<VentanaPrincipal />} />
            <Route path="/about" element={<About />} />
            <Route path="/contact" element={<Contact />} />
            <Route path="/busqueda" element={<BusquedaEventos />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<Profile setUser={setUser} />} />
            <Route path="/evento/:id" element={<EventDetail />} />
            <Route path="/shoppingCart" element={<ShoppingCart />} />
            <Route path="/tickets" element={<UserTickets />} />
            <Route path="/admin" element={<AdminPanel />} />
            <Route path="/categoria/:nombre" element={<CategoryEvents />} />
            <Route path="/vendedor" element={<VendedorPanel />} />
          </Routes>
        </div>

        {/* ----------------- FOOTER ----------------- */}
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

// ----------------------------------------------------
//  COMPONENTE ProfileDropdown (Componente anidado/local)
// ----------------------------------------------------
  function ProfileDropdown({ children }) {
    const [open, setOpen] = useState(false); // Estado para controlar si el menú desplegable está abierto o cerrado
    
    // Efecto para cerrar el menú desplegable cuando se hace clic fuera de él
    useEffect(() => {
      function handleClick(e) {
        // Verifica si el clic no fue dentro del elemento con la clase ".profile-dropdown"
        if (!e.target.closest(".profile-dropdown")) setOpen(false);
      }
      if (open) document.addEventListener("mousedown", handleClick);
      return () => document.removeEventListener("mousedown", handleClick); // Limpieza del event listener
    }, [open]); // Se re-ejecuta cuando 'open' cambia

    return (
      <div className="profile-dropdown relative">
        <button onClick={() => setOpen((o) => !o)} className=" flex items-center justify-center">
          {/* Muestra la foto de perfil o un icono predeterminado */}
          {user?.foto ? (
            <img src={getImageSrc(user.foto)} alt="Foto perfil" className="w-10 h-10 rounded-full object-cover border hover:scale-105 transition" />
          ) : (
            <span
              className="material-symbols-outlined hover:bg-blue-800 rounded-full p-1 transition-colors cursor-pointer"
              style={{ fontSize: "30px" }}>person</span>
          )}
        </button>

        {/* Renderiza el contenido hijo (enlaces del menú) solo si 'open' es true */}
        {open && (
          <div className="absolute right-0 mt-2 w-40 bg-gray-800 rounded shadow-lg z-10">
            {children}
          </div>
        )}
      </div>
    );
  }
}