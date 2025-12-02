import AuthForm from "../components/AuthForm";
import config from "../config/config"


//---------------
// Componente Principal: Register
//-------------

/**
 * Componente de la página de registro de nuevos usuarios.
 * Gestiona el envío del formulario de registro y realiza un intento de login
 * automático si el registro es exitoso.
 */
export default function Register() {

  /**
   * Manejador asíncrono para la lógica de registro de usuario.
   * Realiza la llamada a la API para crear el usuario y, si es exitoso, intenta el login automático.
   * @param {object} data - Objeto con los datos del nuevo usuario (nombre, email, password, etc.).
   */
  const handleRegister = async (data) => {
    try {
      // 1. Petición de Registro
      // Llama al endpoint de registro de la API.
      const res = await fetch(`${config.apiBaseUrl}/tfg/usuario/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      // Log de registro para depuración.
      console.log("Datos enviados al backend (registro):\n", JSON.stringify(data, null, 2));
      const result = await res.json();
      console.log("Respuesta completa del backend (registro):\n", JSON.stringify(result, null, 2));


      // 2. Manejo de Errores de Registro
      if (!res.ok) { // Si la respuesta HTTP no fue exitosa (ej. 400, 500), muestra el error.
        alert(result.message || "Error en registro");
        return; // Detiene la ejecución si hay un error.
      }


      // 3. Intento de Login Automático
      // Si el registro fue exitoso (res.ok), intentamos iniciar sesión automáticamente.
      const loginRes = await fetch(`${config.apiBaseUrl}/tfg/usuario/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        // Usamos solo email y password de los datos originales del registro.
        body: JSON.stringify({ email: data.email, password: data.password }),
      });

      //Log de intento de login automático
      console.log("Intentando login automático tras registro...");
      console.log("Datos enviados al backend (login automático):\n", JSON.stringify({ email: data.email, password: data.password }, null, 2));
      const loginResult = await loginRes.json();
      console.log("Respuesta completa del backend (login automático):\n", JSON.stringify(loginResult, null, 2));

      // 4. Verificación y Redirección tras Login Automático
      // Considera el login automático exitoso si res.ok Y el cuerpo de la respuesta indica éxito o tiene datos de usuario.
      if (loginRes.ok && (loginResult.exito === true || !!loginResult.dtousuarioBajada)) {
        
        // Guardar usuario y token si existen y redirigir
        localStorage.setItem("user", JSON.stringify(loginResult.dtousuarioBajada)); // Guarda los datos del usuario en localStorage.
        if (loginResult.token) {
          localStorage.setItem("token", loginResult.token); // Guarda el token JWT en localStorage.
        }
        alert("Registro y login automáticos correctos ✅");
        window.location.href = "/"; // Redirige a la página principal.
      } else {
        
        // Registro ok pero login automático falló: avisar y quedarse en /register
        alert(loginResult.mensaje || "Usuario creado, pero fallo en login automático. Inicia sesión manualmente.");
      }
    } catch (err) {
      console.error(err);
      alert("Error en el servidor");
    }
  };

//---------------
// Seccion: Renderizado
//-------------

// Renderiza el componente AuthForm.
// Le pasa el tipo "register" y el manejador 'handleRegister' como prop 'onSubmit'.
  return <AuthForm type="register" onSubmit={handleRegister} />;
}