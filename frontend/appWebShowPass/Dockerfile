# Dockerfile para el Frontend Web (React/Vite)

# FASE 1: Construcción (Build)
# Usa la imagen base específica de Node.js versión 22.18.0 (usamos 'alpine' para ser ligeros)
FROM node:22.18.0-alpine AS build

WORKDIR /app

# Copia los archivos de configuración
COPY package.json package-lock.json ./

# Instala las dependencias
# Nota: Si usas 'yarn' o 'pnpm', reemplaza 'npm install'
RUN npm install

# Copia el código fuente
COPY . .

# Compila la aplicación para producción (AJUSTE CRÍTICO AQUÍ)
# La variable VITE_API_URL debe apuntar al nombre del servicio y puerto interno de Docker Compose
RUN VITE_API_URL=http://localhost:8080 npm run build

# Compila la aplicación para producción (esto genera la carpeta 'dist')
# Asegúrate de que este comando coincida con el script 'build' en tu package.json
RUN npm run build

# FASE 2: Ejecución (Run)
# Usa una imagen ligera de Nginx para servir los archivos estáticos
FROM nginx:1.29.3-alpine

# Copia los archivos compilados de la Fase 1 al directorio de servicio de Nginx
# El directorio por defecto de Vite es 'dist'
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf


# Expone el puerto por defecto de Nginx
EXPOSE 80

# Nginx se inicia automáticamente al iniciar el contenedor
CMD ["nginx", "-g", "daemon off;"]