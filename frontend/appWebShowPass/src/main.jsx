// Importa el componente StrictMode desde la librería de React.
// StrictMode es una herramienta para destacar problemas potenciales en una aplicación.
import { StrictMode } from 'react'
// Importa la función 'createRoot' desde 'react-dom/client'.
// Esta es la API moderna y recomendada para renderizar en React 18+.
import { createRoot } from 'react-dom/client'
// Importa el archivo CSS principal para aplicar estilos globales a la aplicación.
import './index.css'
// Importa el componente principal de la aplicación, generalmente llamado 'App'.
// Toda la interfaz de usuario se construye a partir de este componente.
import App from './App.jsx'
// Localiza el elemento DOM con el ID 'root' en el HTML (usualmente en 'index.html').
// Este es el contenedor donde se montará toda la aplicación React.

createRoot(document.getElementById('root')).render(
  // La función 'render' monta y muestra el contenido de React en el contenedor DOM.
  // El contenido a renderizar es el siguiente:
  <StrictMode>
    <App /> {/* Renderiza el componente principal de la aplicación. */}
  </StrictMode>,
)
