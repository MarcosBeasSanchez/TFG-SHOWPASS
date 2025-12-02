// ----------------------------------------------------
// Configuración de la aplicación frontend (config.js o similar)
// ----------------------------------------------------

// ----------------------------------------------------
// 1. Bloque de configuración comentado para DESARROLLO LOCAL
// ----------------------------------------------------
// Estas líneas muestran cómo se configuraría si estuvieras desarrollando localmente
// y tu backend corriera en el puerto 8080 (Java/Spring, Node.js, etc.).


// Cambia 'TU_IP_LOCAL' por la IP de tu máquina en la red local o 'localhost' si solo la usas localmente
// const TU_IP_LOCAL = '' // IPV4  ' ;
// const config = {
//     apiBaseUrl: `http://${TU_IP_LOCAL}:8080`,
//     corsOptions: {
//         origin: `http://${TU_IP_LOCAL}:5173`,
//         credentials: true
//     }
// };


// ----------------------------------------------------
// 2. Bloque de configuración activo para DOCKER/PRODUCCIÓN
// ----------------------------------------------------
// Importa una variable de entorno para la URL del backend
const BACKEND_URL = import.meta.env.VITE_API_URL;

const config = {
    //  Usar la variable de entorno inyectada.
    apiBaseUrl: BACKEND_URL || 'http://localhost:8080', // igual que la del dockerfile

    // Opciones de configuración de CORS (Control de Acceso de Origen Cruzado)
    corsOptions: {
        // En Docker, el frontend corre en puerto 80. Usaremos la misma lógica para desarrollo.
        origin: 'http://localhost:5173', 
        credentials: true
    }
};

export default config;