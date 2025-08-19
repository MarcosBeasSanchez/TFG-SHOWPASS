import { BrowserRouter, Routes, Route, Link, Form } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import VentanaPrincipal from "./pages/VentanaPrincipal";
import { useEffect, useState } from "react";

export default function App() {

  const[user, setUser] = useState(null);
  
  useEffect(() =>{
    const savedUser = localStorage.getItem("user");
    if(savedUser){
      setUser(JSON.parse(savedUser));
    }
  }, []);

  const handleLogout = () =>{
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    window.location.href = "/";
  };

  return (
    <BrowserRouter>
      <nav className="p-4 bg-gray-800 text-white flex gap-4 justify-end">
        {!user ?(
          <>
            <Link to="/login" className="hover:underline">Login</Link>
            <Link to="/register" className="hover:underline">Registro</Link>
          </>
        ) : (
          <div className="flex gap-4 items-center">
            <span>👋 Hola, {user.name}</span>
            <button
              onClick={handleLogout}
              className="bg-red-500 px-3 py-1 rounded hover:bg-red-600"
            >
              Logout
            </button>
          </div>
        )}
      </nav>

      <Routes>
        <Route path="/" element={<VentanaPrincipal/>}/>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
    </BrowserRouter>
  );
}

