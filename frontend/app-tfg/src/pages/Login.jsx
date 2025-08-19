import AuthForm from "../components/AuthForm";
import { colors } from "../styles/colors";


export default function Login() {
  const handleLogin = async (data) => {
    try {
      const res = await fetch("http://localhost:4000/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      const result = await res.json();

      if (!res.ok) {
        alert(result.message || "Error en login");
        return;
      }
      
      // Guardar token y nombre del usuario en localStorage
      localStorage.setItem("token", result.token);
      localStorage.setItem("user", JSON.stringify(result.user));

      alert("Login exitoso ✅");
      window.location.href = "/"; //redirigir a la ventana principal

    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }
  };

  return <AuthForm type="login" onSubmit={handleLogin} />;
}
