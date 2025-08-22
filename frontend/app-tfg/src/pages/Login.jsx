// ...existing code...
import AuthForm from "../components/AuthForm";
import config from "../config/config";

export default function Login() {
  const handleLogin = async (data) => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      const result = await res.json();

      // Considera éxito solo si el backend indica exito o devuelve el usuario
      const isSuccess = res.ok && (result.exito === true || !!result.dtousuarioBajada);

      if (!isSuccess) {
        alert(result.mensaje || "Error en login");
        return; // No redirige si hay error
      }

      localStorage.setItem("user", JSON.stringify(result.dtousuarioBajada));
      if (result.token) {
        localStorage.setItem("token", result.token);
      }

      alert(result.mensaje || "Login exitoso ✅");
      // Redirige solo cuando realmente hubo éxito
      window.location.href = "/";
    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
      // No redirige si hay error
    }
  };

  return <AuthForm type="login" onSubmit={handleLogin} />;
}
