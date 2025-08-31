import datosDesarrolladores from "../config/datosDesarrolladores";

export default function Contact() {
    // Extraer los desarrolladores en un array
    const devs = [datosDesarrolladores.marcos, datosDesarrolladores.dylan];

    return (
        <section className="">
            <div className="w-full p-6 sm:p-8 text-gray-900 shadow-lg rounded-lg">
                <h1 className="text-3xl font-bold mb-6 text-center">Contáctanos</h1>

                <div className="w-full flex justify-center mt-8">
                    <iframe
                        src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d6076.256045189071!2d-3.6002503999999997!3d40.4060146!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd42255d3a247575%3A0xd8a0a40edf810cff!2sIES%20Villablanca!5e0!3m2!1ses!2ses!4v1756133631395!5m2!1ses!2ses"
                        width="1920"
                        height="450"
                        style={{ border: 0 }}
                        allowFullScreen="true"
                        loading="lazy"
                        referrerPolicy="no-referrer-when-downgrade"
                        title="IES Villablanca Location"
                        className="rounded-lg shadow-lg"
                    ></iframe>
                </div>

                <ul className="flex flex-row gap-10 md:gap-20 justify-center items-center w-full my-10">
                    {devs.map((dev, idx) => (
                        <li key={idx} className="flex flex-col items-center bg-gray-100/80 backdrop-blur-md p-4 rounded-lg flex-1 max-w-xs">
                            <img
                                src={dev.photo}
                                alt={dev.nombre}
                                className="w-20 h-20 rounded-full mb-2 border mx-auto"
                            />
                            <h2 className="text-lg font-semibold text-center">{dev.nombre}</h2>
                            <p className="text-gray-700 text-center">{dev.descripcion}</p>
                            <a
                                href={dev.github}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-blue-600 hover:underline text-sm mt-2 text-center"
                            >
                                <span className="material-symbols-outlined" style={{ fontSize: "16px", verticalAlign: "middle" }}>
                                    link
                                </span>
                                GitHub
                            </a>
                        </li>
                    ))}
                </ul>
            </div>

            <div className="w-full p-8  bg-blue-400/80 backdrop-blur-md text-white-900 shadow-lg rounded-lg ">
                <h2 className="text-2xl font-bold mb-4 text-center">¿Tienes alguna pregunta?</h2>
                <p className="text-black text-center">
                    No dudes en ponerte en contacto con nosotros.
                </p>
            </div>
        </section>
    );
}