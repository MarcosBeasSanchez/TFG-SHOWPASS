import { useState } from "react";

export default function AuthForm({ type, onSubmit }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [birthDate, setBirthDate] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (type === "register") {
      onSubmit({ name, email, password, birthDate });
    } else {
      onSubmit({ email, password });
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen min-w-full">

    
      <form
        onSubmit={handleSubmit}
        className="max-w-md w-full bg-white p-8 rounded-xl shadow space-y-4"
      >
        {/* TITULO */}
        <h2 className="text-2xl font-bold text-center text-gray-500">
          {type === "register" ? "CREAR CUENTA" : "INICIAR SESIÓN"}
        </h2>

        {/* Nombre */}
        {type === "register" && (
          <input
            type="text"
            placeholder="Nombre"
            value={name}
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

            value={birthDate}
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
