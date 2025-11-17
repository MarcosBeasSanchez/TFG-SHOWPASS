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

      console.log("Datos enviados al backend (login):\n",JSON.stringify(data, null, 2));
      
      const result = await res.json();

      console.log("Respuesta completa del backend (login):\n",JSON.stringify(result, null, 2));

      // Considera éxito solo si el backend indica exito o devuelve el usuario
      const isSuccess = res.ok && (result.exito === true || !!result.dtousuarioBajada);

      if (!isSuccess) {
        alert(result.mensaje || "Error en login");
        return; // No redirige si hay error
      }

      localStorage.setItem("user", JSON.stringify(result.dtousuarioBajada));
      // Guardar el token si existe
      if (result.token) {
        localStorage.setItem("token", result.token);
      }
      //Mensaje
      alert(result.mensaje || "Login exitoso ✅");

      console.log("Datos obtenidos del endpoint de login:", result);

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
