import AuthForm from "../components/AuthForm";

export default function Register() {
  const handleRegister = async (data) => {
    try {
      const res = await fetch("http://localhost:4000/api/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      const result = await res.json();

      if (!res.ok) {
        alert(result.message || "Error en registro");
        return;
      }

      alert("Usuario creado ✅ Ahora inicia sesión");
    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }
  };

  return <AuthForm type="register" onSubmit={handleRegister} />;
}
