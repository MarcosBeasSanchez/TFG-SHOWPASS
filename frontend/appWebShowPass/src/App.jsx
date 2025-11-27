import { BrowserRouter, Routes, Route, Link, useNavigate } from "react-router-dom";
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
import UserTickets from "./pages/Tickets.jsx";
import { useEffect, useState } from "react";
import AdminPanel from "./pages/AdminPanel";
import VendedorPanel from "./pages/VendedorPanel";
import config from "./config/config";


export default function App() {
  const [user, setUser] = useState(null);
  const [drawerOpen, setDrawerOpen] = useState(false);

  {/* Estado de carga de autenticación*/ }
  const [loadingAuth, setLoadingAuth] = useState(true); // Nuevo estado de carga de autenticación

  {/* Estado para el modo oscuro */ }
  const [darkMode, setDarkMode] = useState(false);

  {/* Aplicar o quitar la clase 'dark' al elemento raíz según el estado de darkMode */ }
  useEffect(() => {
    const root = document.documentElement;
    if (darkMode) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }, [darkMode]);

  // Lógica para el AUTO-LOGIN al cargar la aplicación
    useEffect(() => {
        const savedToken = localStorage.getItem("token");
        
        const checkAuth = async () => {
            setLoadingAuth(true);

            if (savedToken) {
                try {
                    // 1. Llamada al endpoint protegido 'perfil' para autologin
                    const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/perfil`, {
                        method: 'GET',
                        headers: { 
                            // CLAVE: Adjuntar el token guardado en el encabezado
                            'Authorization': `Bearer ${savedToken}`, 
                            'Content-Type': 'application/json' 
                        }
                    });

                    if (res.ok) {
                        // Caso A:  Éxito (Código 200 OK) - El token es válido
                        const data = await res.json();
                        setUser(data); // Establece los datos actualizados del perfil
                        localStorage.setItem("user", JSON.stringify(data)); // Opcional: Actualizar el localStorage con datos frescos
                        console.log("Auto-Login Exitoso. Sesión persistente activa.");
                    } else if (res.status === 401) {
                        // Caso B: Falla (Código 401 Unauthorized) - Token expirado/inválido
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
                    // Decides si quieres forzar el logout o dejar la app en estado "deslogueado"
                    // Si el backend no está disponible, no hacemos nada más que informar.
                }
            }
            
            setLoadingAuth(false);
        };

        checkAuth();
    }, []); // Se ejecuta SÓLO una vez al montar el componente App

    // Muestra el estado de carga mientras se valida la sesión
    if (loadingAuth) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <p className="text-xl">Cargando sesión y datos iniciales...</p>
            </div>
        );
    }


  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    window.location.href = "/";
  };

  const getImageSrc = (img) => {
    if (!img) return null; // si no hay imagen, devolvemos vacío
    if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
    if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
    if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
    return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
  };


  return (
    <BrowserRouter>
      <div className="flex flex-col min-h-screen ">
        <nav className="pl-2 pr-2 sm:pl-8  pt-4 pb-4 bg-blue-950 text-white flex items-center justify-between gap-2 sm:pr-8 sm:gap-0">

          {/* Logo */}
          <div className="flex items-center gap-4 ">
            <Link to="/">
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

          {/* Categorías y opciones en horizontal solo en escritorio */}
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

          {/* Bloque derecho: login/profile */}
          <div className="hidden sm:flex gap-4 items-center relative ">
            {!user ? (
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
            ) : (
              <div className="flex items-center gap-4">
                <span
                  className="material-symbols-outlined hover:bg-blue-800 rounded-full p-1 transition-colors cursor-pointer"
                  title={darkMode ? "Desactivar modo oscuro" : "Activar modo oscuro"}
                  onClick={() => setDarkMode((prev) => !prev)}
                >
                  {darkMode ? "light_mode" : "dark_mode"}
                </span>
                <ProfileDropdown>
                  <span className="flex w-full text-left text-sm items-center justify-items-center-safe px-2 py-2 text-white">
                    👋 Hola, {user?.nombre}
                  </span>

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
                    to="/tickets"
                    className="flex w-full text-left items-center justify-items-center-safe px-2 py-2 hover:bg-gray-700"
                  >
                    <span className="material-symbols-outlined pr-2 ">
                      qr_code
                    </span>
                    Ver tickets
                  </Link>

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

        {/* Drawer lateral para móvil */}
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
                {!user ? (
                  <>
                    <Link to="/login" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>LOGIN</Link>
                    <Link to="/register" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>REGISTRO</Link>
                  </>
                ) : (
                  <>
                    <Link to="/profile" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>
                      <span className="material-symbols-outlined pr-2">manage_accounts</span>
                      Editar perfil
                    </Link>
                    <Link to="/shoppingCart" className="text-base px-2 py-2 hover:bg-blue-800 rounded" onClick={() => setDrawerOpen(false)}>
                      <span className="material-symbols-outlined pr-2 align-middle">shopping_cart</span>
                      Ver carrito
                    </Link>
                    <Link
                      to="/tickets"
                      className="text-base px-2 py-2 hover:bg-blue-800 rounded **flex items-center**"
                      onClick={() => setDrawerOpen(false)}
                    >
                      <span className="material-symbols-outlined pr-2 align-middle ">qr_code</span>
                      Ver tickets
                    </Link>

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

                    <button className="flex items-center  px-2 py-2 gap-2 hover:bg-blue-800 rounded" onClick={() => setDarkMode((prev) => !prev)}>
                      <span className="material-symbols-outlined ">
                        dark_mode
                      </span> {darkMode ? "Modo claro" : "Modo oscuro"}
                    </button>

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

        {/* Rutas */}
        <div className="flex-1">
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

  function ProfileDropdown({ children }) {
    const [open, setOpen] = useState(false);

    useEffect(() => {
      function handleClick(e) {
        if (!e.target.closest(".profile-dropdown")) setOpen(false);
      }
      if (open) document.addEventListener("mousedown", handleClick);
      return () => document.removeEventListener("mousedown", handleClick);
    }, [open]);

    return (
      <div className="profile-dropdown relative">
        <button onClick={() => setOpen((o) => !o)} className=" flex items-center justify-center">
          {user?.foto ? (
            <img src={getImageSrc(user.foto)} alt="Foto perfil" className="w-10 h-10 rounded-full object-cover border hover:scale-105 transition" />
          ) : (
            <span
              className="material-symbols-outlined hover:bg-blue-800 rounded-full p-1 transition-colors cursor-pointer"
              style={{ fontSize: "30px" }}>person</span>
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