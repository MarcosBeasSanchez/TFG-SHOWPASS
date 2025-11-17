 🧩 Configuración del entorno para el microservicio de recomendación

## 🧱 Requisitos previos

Antes de empezar, asegúrate de tener instalado lo siguiente:

- **Python 3.11.0**
- **Visual Studio** con la opción **Desarrollo para escritorios con C++**

---

## ⚙️ Configuración del entorno virtual

Dentro de la carpeta del proyecto **`tfg/recomendacionServicio`**, abre una consola (puede ser la del **Visual Studio Code** o la del sistema) y ejecuta los siguientes comandos:

```bash
# Crear el entorno virtual
python -m venv venv

# Activar el entorno virtual (Windows)
venv\Scripts\activate
```

> 💡 Este paso solo se realiza una vez por ordenador.  
> Si estás en macOS o Linux, usa:
> ```bash
> source venv/bin/activate
> ```

---

## 📦 Instalación de dependencias

Con el entorno virtual activado, instala todas las dependencias necesarias:

```bash
pip install -r requirements.txt
```

Este archivo (`requirements.txt`) ya contiene todas las librerías necesarias para ejecutar el microservicio.

---

## 🚀 Ejecución del microservicio

Una vez completada la instalación, puedes iniciar el servicio con:

```bash
uvicorn main:app --reload --port 8000
```

El microservicio se ejecutará en el puerto **8000**.  
Puedes acceder desde tu navegador o realizar peticiones a los endpoints definidos en la API.

---

## 🐳 Nota sobre Docker

Si ejecutas este proyecto dentro de **Docker**, no es necesario realizar todos los pasos anteriores.  
Docker se encargará de instalar las dependencias y configurar el entorno automáticamente.

---

## 🌐 Integración con la web y la app móvil

- En la **web**, puedes consumir los endpoints del microservicio para mostrar la información recomendada.  
- En la **app móvil**, las recomendaciones se muestran:
  - En la **información del evento** → lista de eventos relacionados o parecidos.
  - En el **carrito** → lista de eventos basados en el historial del usuario.
  - Si el historial está vacío, se mostrará una lista aleatoria de eventos.

---

✍️ *Autor: Dylan GS DAM*  
📅 *Última actualización: 13/11/2025*
