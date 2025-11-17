import React from 'react';
import datosDesarroladores from "../config/datosDesarrolladores";
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

export default function About() {
  return (
    <div className="flex items-center justify-center min-h-screen px-4 py-4">
      <div className="w-full max-w-4xl p-6 sm:p-8 bg-white text-gray-900 shadow-lg rounded-lg oscuro">
        <h1 className="text-3xl font-bold mb-6">Sobre Nosotros</h1>
        <p className="mb-4">
          Somos dos estudiantes apasionados por la tecnología y el desarrollo de software. Este proyecto es nuestro Trabajo de Fin de Grado (TFG), donde hemos puesto en práctica todos los conocimientos adquiridos durante el Grado Superior de
          <span className="font-bold"> Desarrollo de Aplicaciones Multiplataforma.</span>
        </p>
        <p className="mb-6">
          Nuestra misión es facilitar la compra de entradas para eventos, conciertos y espectáculos a través de una plataforma web y una app móvil moderna, intuitiva y segura. Creemos que la tecnología puede mejorar la experiencia de los usuarios y acercarles a sus eventos favoritos de manera sencilla.
        </p>
        <h2 className="text-xl font-semibold mb-4">¿Quiénes somos?</h2>
        <div className="flex flex-col sm:flex-row sm:justify-evenly gap-8 my-10">
          {members.map((member, idx) => (
            <div key={idx} className="flex flex-col items-center bg-gray-100 p-4 w-full sm:w-80 justify-center rounded-lg fondoOscuro">
              <img
                src={member.photo}
                alt={member.name}
                className="w-24 h-24 sm:w-28 sm:h-28 rounded-full mb-2"
              />
              <strong className="text-lg text-center">{member.name}</strong>
              <span className="text-sm text-gray-500 text-center">{member.role}</span>
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
        <p>
          Juntos hemos trabajado en cada aspecto de este proyecto, desde el diseño del backend y la base de datos hasta la implementación junto con el Frontend y diseños de interfaces, con el objetivo de crear una solución completa y funcional.
        </p>
      </div>
    </div>
  );
}