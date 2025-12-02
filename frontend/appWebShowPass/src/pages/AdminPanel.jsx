import React, { useState } from 'react';

// Componentes de secciones administrativas
import AdminUserSection from '../pages/AdminUserSection';
import AdminEventSection from '../pages/AdminEvent';

/**
 * Componente principal del Panel de Administración.
 * Funciona como un contenedor y manejador de navegación para las secciones de administración.
 */
const AdminPanel = () => {
    // Estado que almacena la sección activa ('user', 'event' o null si no hay ninguna seleccionada).
    const [activeSection, setActiveSection] = useState(null);

    // Objeto de mapeo para obtener el título descriptivo de la sección activa.
    const sectionTitles = {user: 'Usuarios',event: 'Eventos'};

    // ----------------------------------------------------
    //  RENDERIZADO (JSX)
    // ----------------------------------------------------
    return (
        <div className='min-h-screen p-4'>
            <div className='max-w-5xl mx-auto p-6 bg-white shadow rounded oscuro'>
                {/* Título principal del Panel */}
                <h1 className='text-2xl font-bold text-gray-600  oscuroTextoGris mb-6'>Panel de Administración</h1>

                {/* Botones de navegación */}
                <div className='flex flex-col md:flex-row justify-center  mt-8 gap-5'>
                    
                    {/* Botón para la sección de Usuarios */}
                    <section className="flex-1">
                        <button
                            className={`w-full text-white bg-blue-500 p-3 px-6  shadow hover:bg-blue-700 transition font-semibold text-md flex items-center justify-center gap-2 `}
                            onClick={() => setActiveSection(activeSection === 'user' ? null : 'user')}
                        >
                            <span className="material-symbols-outlined">group</span>
                            Usuarios
                        </button>
                    </section>

                    {/* Botón para la sección de Eventos */}
                    <section className="flex-1">
                        <button
                            className={`w-full text-white bg-blue-500 p-3 px-6  shadow hover:bg-blue-700 transition font-semibold text-md flex items-center justify-center gap-2`}
                            onClick={() => setActiveSection(activeSection === 'event' ? null : 'event')}
                        >
                            <span className="material-symbols-outlined">event</span>
                            Eventos
                        </button>
                    </section>
                </div>

                {/* Contenedor principal del contenido de la sección activa */}
                <div className='mt-10'>
                    {activeSection && (
                        <h2 className='text-xl font-bold text-gray-600 oscuroTextoGris mb-4'>
                            {sectionTitles[activeSection]}
                        </h2>
                    )}

                    {/* Usuarios */}
                    {activeSection === 'user' && <AdminUserSection />}
                    {/* Eventos */}
                    {activeSection === 'event' && <AdminEventSection />}
                    
                </div>
            </div>
        </div>
    );
};

export default AdminPanel;