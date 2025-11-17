# Dockerfile para el Servicio de Recomendación (Python/FastAPI)

# Usa la imagen base de Python estable
FROM python:3.11-slim

# Establece el directorio de trabajo
WORKDIR /app

# --- PASO DE ARREGLO DE ERROR GCC/COMPILACIÓN ---
# Instala las herramientas de compilación necesarias para bibliotecas con componentes C/C++ 
# como scikit-surprise, numpy, etc.
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    gcc \
    python3-dev \
    build-essential && \
    rm -rf /var/lib/apt/lists/*
# ------------------------------------------------

# Copia el archivo de requisitos e instálalos
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copia el resto del código de la aplicación (incluyendo main.py)
COPY . .

# Expone el puerto por defecto de FastAPI (ej: 8000)
EXPOSE 8000

# Comando para iniciar la aplicación con Uvicorn (tu comando original)
# 'main:app' asumiendo que tu objeto FastAPI se llama 'app' en 'main.py'
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]