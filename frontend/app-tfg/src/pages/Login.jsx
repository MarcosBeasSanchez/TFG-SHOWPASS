import AuthForm from "../components/AuthForm";

export default function Login() {
  const handleLogin = async (data) => {
    try {
      const res = await fetch("http://localhost:8080/tfg/usuario/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      const result = await res.json();

      if (!res.ok) {
        alert(result.message || "Error en login");
        return;
      }


      // Guardar datos del usuario en localStorage
      localStorage.setItem("user", JSON.stringify(result.dtousuarioBajada));

      // Guardar token solo si existe
      if (result.token) {
        localStorage.setItem("token", result.token);
      }

      // Mostrar el mensaje recibido del backend
      alert(result.mensaje || "Login exitoso ✅");
      window.location.href = "/"; //redirigir a la ventana principal 

    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }
  };

  return <AuthForm type="login" onSubmit={handleLogin} />;
}
