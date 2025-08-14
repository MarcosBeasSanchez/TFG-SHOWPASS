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

      localStorage.setItem("token", result.token);
      alert("Login exitoso ✅");
    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }
  };

  return <AuthForm type="login" onSubmit={handleLogin} />;
}
