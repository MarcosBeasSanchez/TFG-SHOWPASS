import AuthForm from "../components/AuthForm";
import config from "../config/config"

export default function Register() {
  const handleRegister = async (data) => {
    try {
      const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      const result = await res.json();

      console.log(result);

      if (!res.ok) {
        alert(result.message || "Error en registro");
        return;
      }

      const loginRes = await fetch(`${config.apiBaseUrl}/tfg/usuario/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: data.email, password: data.password }),
      });

      const loginResult = await loginRes.json();

      if (loginRes.ok && (loginResult.exito === true || !!loginResult.dtousuarioBajada)) {
        // Guardar usuario y token si existen y redirigir
        localStorage.setItem("user", JSON.stringify(loginResult.dtousuarioBajada));
        if (loginResult.token) {
          localStorage.setItem("token", loginResult.token);
        }
        alert(loginResult.mensaje || "Registro y login automáticos correctos ✅");
        window.location.href = "/";
      } else {
        // Registro ok pero login automático falló: avisar y quedarse en /register
        alert(loginResult.mensaje || "Usuario creado, pero fallo en login automático. Inicia sesión manualmente.");
      }
    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }
  };

  return <AuthForm type="register" onSubmit={handleRegister} />;
}