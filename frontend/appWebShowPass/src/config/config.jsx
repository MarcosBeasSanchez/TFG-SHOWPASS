// Configuración de la aplicación frontend 

//LOCALHOST PARA DESARROLLO LOCAL
// Cambia 'TU_IP_LOCAL' por la IP de tu máquina en la red local o 'localhost' si solo la usas localmente

// const TU_IP_LOCAL = '' // IPV4  ' ;
// const config = {
//     apiBaseUrl: `http://${TU_IP_LOCAL}:8080`,
//     corsOptions: {
//         origin: `http://${TU_IP_LOCAL}:5173`,
//         credentials: true
//     }
// };


// CON DOCKER 
const BACKEND_URL = import.meta.env.VITE_API_URL;

const config = {
    // 🛑 CAMBIO CLAVE: Usar la variable de entorno inyectada.
    apiBaseUrl: BACKEND_URL || 'http://localhost:8080', // igual que la del dockerfile

    // Opcional: El puerto de desarrollo de Vite (5173) debe ser manejado por el servidor
    // o debe eliminarse si el CORS se maneja en el backend.
    corsOptions: {
        // En Docker, el frontend corre en puerto 80. Usaremos la misma lógica para desarrollo.
        // Si necesitas esto para desarrollo local, puedes mantener 'localhost:5173'.
        origin: 'http://localhost:5173', 
        credentials: true
    }
};

export default config;