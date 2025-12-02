// Importa el objeto de datos estáticos que contiene la información de los desarrolladores.
import datosDesarroladores from "../config/datosDesarrolladores";
// ----------------------------------------------------
// 1. PREPARACIÓN DE DATOS
// ----------------------------------------------------
// Array de miembros creado a partir de los datos importados.
// Se usa el encadenamiento opcional (?) para evitar errores si algún campo falta,
// aunque en un archivo de datos estático es menos probable.
const members = [
  {
    name: datosDesarroladores.marcos?.nombre,
    role: datosDesarroladores.marcos?.descripcion,
    photo: datosDesarroladores.marcos?.photo,
    github: datosDesarroladores.marcos?.github,
  },
  {
    name: datosDesarroladores.dylan?.nombre,
    role: datosDesarroladores.dylan?.descripcion,
    photo: datosDesarroladores.dylan?.photo,
    github: datosDesarroladores.dylan?.github,
  },
];

/**
 * Componente funcional 'About' (Sobre Nosotros).
 * Muestra información del proyecto y los perfiles de los desarrolladores.
 */
export default function About() {
  return (
    // Contenedor principal para centrar el contenido vertical y horizontalmente
    <div className="flex items-center justify-center min-h-screen px-4 py-4">

      {/* Contenedor del contenido con estilo de tarjeta (sombra, fondo blanco, esquinas redondeadas) */}
      <div className="w-full max-w-4xl p-6 sm:p-8 bg-white text-gray-900 shadow-lg rounded-lg oscuro">
        
        {/* Título de la página */}
        <h1 className="text-3xl font-bold mb-6">Sobre Nosotros</h1>

        {/* Párrafo introductorio sobre el propósito del proyecto (TFG) */}
        <p className="mb-4">
          Somos dos estudiantes apasionados por la tecnología y el desarrollo de software. Este proyecto es nuestro Trabajo de Fin de Grado (TFG), donde hemos puesto en práctica todos los conocimientos adquiridos durante el Grado Superior de
          <span className="font-bold"> Desarrollo de Aplicaciones Multiplataforma.</span>
        </p>

        {/* Párrafo sobre la misión de la plataforma (venta de entradas) */}
        <p className="mb-6">
          Nuestra misión es facilitar la compra de entradas para eventos, conciertos y espectáculos a través de una plataforma web y una app móvil moderna, intuitiva y segura. Creemos que la tecnología puede mejorar la experiencia de los usuarios y acercarles a sus eventos favoritos de manera sencilla.
        </p>

        {/* Subtítulo de la sección de miembros */}
        <h2 className="text-xl font-semibold mb-4">¿Quiénes somos?</h2>
        <div className="flex flex-col sm:flex-row sm:justify-evenly gap-8 my-10">

          {/* Tarjeta individual del miembro */}
          {members.map((member, idx) => (
            <div key={idx} className="flex flex-col items-center bg-gray-100 p-4 w-full sm:w-80 justify-center rounded-lg fondoOscuro">
              {/* Imagen de perfil */}
              <img
                src={member.photo}
                alt={member.name}
                className="w-24 h-24 sm:w-28 sm:h-28 rounded-full mb-2"
              />
              {/* Nombre del desarrollador */}
              <strong className="text-lg text-center">{member.name}</strong>

              {/* Rol o descripción */}
              <span className="text-sm text-gray-500 text-center">{member.role}</span>

              {/* Enlace a GitHub */}
              <a
                href={member.github}
                target="_blank"
                rel="noopener noreferrer"
                className="mt-2 text-blue-600 hover:underline text-sm"
              >
                GitHub
              </a>
            </div>
          ))}
        </div>
        {/* Párrafo de conclusión sobre la colaboración en el proyecto */}
        <p>
          Juntos hemos trabajado en cada aspecto de este proyecto, desde el diseño del backend y la base de datos hasta la implementación junto con el Frontend y diseños de interfaces, con el objetivo de crear una solución completa y funcional.
        </p>
      </div>
    </div>
  );
}