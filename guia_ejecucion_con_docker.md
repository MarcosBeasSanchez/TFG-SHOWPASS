# 🚀 Guía Rápida: Inicio del Proyecto TFG ShowPass con Docker Compose

Esta guía proporciona los comandos esenciales para construir, levantar y
verificar el estado de todos los microservicios (Backend Java, Frontend
Web React y Servicio de Recomendación FastAPI).

## 🛠️ Requisitos Previos

Tener instalado Docker (Docker Engine o Docker Desktop).

Estar ubicado en el directorio raíz de tu proyecto, donde se encuentra
`docker-compose.yml` y el archivo `.env`.

## PASO 1: Construir y Levantar los Servicios

Utiliza este comando para construir las imágenes que no existan (o si ha
habido cambios en los Dockerfiles o en el código fuente) y luego iniciar
todos los contenedores en segundo plano (-d):

    docker compose up --build -d

### Funciones de los parámetros

-   **up**: Crea e inicia los contenedores.\
-   **--build**: Fuerza la reconstrucción de las imágenes antes de
    iniciar.\
-   **-d**: Ejecuta los contenedores en modo detached (segundo plano).

Comando opcional para ver logs en vivo:

    docker compose up --build

## PASO 2: Verificar el Estado

Confirma que todos los contenedores se han iniciado correctamente:

    docker compose ps

Deberías ver los servicios:

-   `showpass_backend`
-   `showpass_recomendador`
-   `showpass_frontend_web`

En estado **running**.

## PASO 3: Pruebas de Acceso y Comunicación

### 1. Backend (Spring Boot) - Puerto 8080

Prueba de datos:

http://localhost:8080/tfg/utilidades/data

### 2. Microservicio de Recomendación (FastAPI) - Puerto 8000

Fuerza el reentrenamiento:

http://localhost:8000/reload

### 3. Frontend Web (React/Nginx) - Puerto 80

Acceso web:

http://localhost/

## PASO 4: Verificación de Logs (Debugging)

### Logs del backend

    docker logs showpass_backend

### Logs del recomendador

    docker logs showpass_recomendador

### Logs del frontend

    docker logs showpass_frontend_web

## PASO 5: Detener y Limpiar

### A. Detener contenedores

    docker compose down

### B. Limpieza profunda (incluye volúmenes)

    docker compose down --volumes
