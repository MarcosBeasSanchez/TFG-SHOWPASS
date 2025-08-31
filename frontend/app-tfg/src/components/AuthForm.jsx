import { useState } from "react";

export default function AuthForm({ type, onSubmit }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nombre, setName] = useState("");
  const [fechaNacimiento, setBirthDate] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (type === "register") {
      onSubmit({ nombre, email, password, fechaNacimiento });
    } else {
      onSubmit({ email, password });
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen min-w-full p-5">

      <h1 className="text-3xl font-bold text-center text-blue-950 mb-10">
        {type === "register" ? "¡BIENVENIDO!" : "¡HOLA DE NUEVO!"}
      </h1>

      <form
        onSubmit={handleSubmit}
        className="max-w-md w-full bg-white p-8 rounded-xl shadow space-y-4"
      >
        {/* TITULO */}
        <h2 className="text-xl font-bold text-center text-gray-500">
          {type === "register" ? "CREAR CUENTA" : "INICIAR SESIÓN"}
        </h2>

        {/* Nombre */}
        {type === "register" && (
          <input
            type="text"
            placeholder="Nombre"
            value={nombre}
            onChange={(e) => setName(e.target.value)}
            className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-black"
            required
          />

        )}
        {/* email */}
        <input
          type="email"
          placeholder="Correo electrónico"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-black"
          required
        />
        {/* contraseña */}
        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-black"
          required
        />
        {/* Fecha de nacimiento */}
        {type === "register" && (
          <input
            type="date"

            value={fechaNacimiento}
            onChange={(e) => setBirthDate(e.target.value)}
            className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
            required
          />

        )}

        <button
          type="submit"
          className="w-full bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition"
        >
          {type === "register" ? "Registrarse" : "Entrar"}
        </button>
      </form>
    </div>
  );
}
