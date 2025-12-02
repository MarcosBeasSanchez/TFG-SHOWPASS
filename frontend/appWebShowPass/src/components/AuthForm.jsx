// Importa el hook useState de React para manejar el estado de los campos del formulario.
import { useState } from "react";


/**
 * Componente de formulario de autenticación reutilizable.
 * * @param {object} props - Propiedades del componente.
 * @param {string} props.type - Define el modo del formulario ('login' o 'register').
 * @param {function} props.onSubmit - Función callback que se ejecuta al enviar el formulario con los datos.
 */
export default function AuthForm({ type, onSubmit }) {

// ----------------------------------------------------
// 1. ESTADO DEL FORMULARIO
// ----------------------------------------------------

  // Campos comunes a ambos formularios
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  // Campos exclusivos del registro
  const [nombre, setName] = useState("");
  const [fechaNacimiento, setBirthDate] = useState("");
  const [rol, setRol] = useState(); // valor por defecto

// ----------------------------------------------------
// 2. MANEJADOR DE ENVÍO
// ----------------------------------------------------
  const handleSubmit = (e) => {
    e.preventDefault(); // Previene el comportamiento por defecto de recarga del formulario HTML

    // ('register' o 'login')
    if (type === "register") {
      onSubmit({ nombre, email, password, fechaNacimiento, rol });
    } else {
      onSubmit({ email, password });
    }
  };
// ----------------------------------------------------
// 3. RENDERIZADO DEL FORMULARIO
// ----------------------------------------------------
  return (
    <div className="flex flex-col items-center justify-center min-h-screen min-w-full p-5">

      {/* BIENVENIDA */}
      <h1 className="text-3xl font-bold text-center text-blue-950 mb-10 oscuroTextoGris">
        {type === "register" ? "¡BIENVENIDO!" : "¡HOLA DE NUEVO!"}
      </h1>

      <form
        onSubmit={handleSubmit}
        className="max-w-md w-full bg-white p-8 rounded-xl shadow space-y-4 oscuro"
      >
        {/* TITULO */}
        <h2 className="text-xl font-bold text-center text-gray-500 ">
          {type === "register" ? "CREAR CUENTA" : "INICIAR SESIÓN"}
        </h2>

        {/* NOMBRE */}
        {type === "register" && (
          <input
            type="text"
            placeholder="Nombre"
            value={nombre}
            onChange={(e) => setName(e.target.value)}
            className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500"
            required
          />

        )}
        {/* EMAIL */}
        <input
          type="email"
          placeholder="Correo electrónico"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500"
          required
        />
        {/* CONTRASEÑA */}
        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500"
          required
        />
        {/* FECHA DE NACIMIENTO */}
        {type === "register" && (
          <> 
            <input
              type="date"
              value={fechaNacimiento}
              onChange={(e) => setBirthDate(e.target.value)}
              className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500"
              required
            />

            {/* ROL */}
            <select
              value={rol}
              onChange={(e) => setRol(e.target.value)}
              required
              className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-gray-500"
            >
              <option value="CLIENTE">CLIENTE</option>
              <option value="VENDEDOR">VENDEDOR</option>
            </select>
          </>
        )}
        
        {/* BOTÓN DE ENVÍO */}
        <button
          type="submit"
          className="w-50 mx-auto mt-5 block bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition "
        >
          {type === "register" ? "Registrarse" : "Entrar"}
        </button>
      </form>
    </div>
  );
}
