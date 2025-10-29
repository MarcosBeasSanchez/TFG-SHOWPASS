// Configuración de la aplicación frontend 
// Cambia 'TU_IP_LOCAL' por la IP de tu máquina en la red local o 'localhost' si solo la usas localmente
const TU_IP_LOCAL = '192.168.0.50';

const config = {
    apiBaseUrl: `http://${TU_IP_LOCAL}:8080`,
    corsOptions: {
        origin: `http://${TU_IP_LOCAL}:5173`,
        credentials: true
    }
};


export default config;