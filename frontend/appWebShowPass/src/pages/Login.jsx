import AuthForm from "../components/AuthForm";
import config from "../config/config";

/**
 * Componente de la página de inicio de sesión.
 * Utiliza el componente genérico AuthForm para manejar la interfaz del formulario
 * y define la lógica de la llamada a la API para autenticar al usuario.
 */
export default function Login() {

  /**
   * Manejador asíncrono que se ejecuta al enviar el formulario de login.
   * Intenta autenticar al usuario contra el backend.
   * @param {object} data - Objeto con las credenciales del usuario (ej: {email: "...", password: "..."}).
   */
  const handleLogin = async (data) => {
    try {

      // 1. Petición al Backend
      // Llama al endpoint de login de la API.
      const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data), // Convierte el objeto de datos a JSON
      });

      // Loguea los datos enviados y la respuesta completa para depuración
      console.log("Datos enviados al backend (login):\n",JSON.stringify(data, null, 2));
      const result = await res.json(); // Parsear la respuesta JSON
      console.log("Respuesta completa del backend (login):\n",JSON.stringify(result, null, 2));

      // 2. Verificación de Éxito
      // Determina el éxito basado en: la respuesta HTTP es OK (res.ok) Y el cuerpo de la respuesta indica exito 
      const isSuccess = res.ok && (result.exito === true || !!result.dtousuarioBajada);
      if (!isSuccess) {
        // Si no hay éxito, muestra el mensaje de error del backend o uno genérico.
        alert(result.mensaje || "Error en login");
        return; // No redirige si hay error
      }

      // 3. Almacenamiento Local (Éxito)
      // Guarda los datos del usuario en el localStorage.
      localStorage.setItem("user", JSON.stringify(result.dtousuarioBajada));
      // Guardar el token
      if (result.token) {
        localStorage.setItem("token", result.token);
      }

      // 4. Feedback y Redirección
      // Muestra un mensaje de éxito.
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

//---------------
// Seccion: Renderizado
//-------------

// Renderiza el componente AuthForm.
// Le pasa el tipo "login" para configurar la interfaz (ej. etiquetas, validaciones)
// y le pasa el manejador 'handleLogin' como prop 'onSubmit' para la lógica de envío.
  return <AuthForm type="login" onSubmit={handleLogin} />;
}
