import { useState } from 'react';
import AddEventSection from '../pages/AddEventSection'; 
import EditEventSection from '../pages/EditEventSection';  


// Objeto para almacenar títulos que podrían usarse en encabezados dinámicos.
const sectionTitles = {
  addEvent: 'Añadir Evento',
  editEvent: 'Editar Evento',
};

//---------------
// Componente Principal: VendedorPanel
//-------------

/**
 * Componente principal del panel de control para vendedores.
 * Permite al usuario alternar entre las funcionalidades de añadir y editar eventos.
 */
const VendedorPanel = () => {

  // Estado que almacena la sección activa ('addEvent', 'editEvent', o null si ninguna está abierta).
  const [activeSection, setActiveSection] = useState(null);

  return (
    <div className='min-h-screen p-4'>
      <div className='max-w-5xl mx-auto p-6 bg-white shadow rounded oscuro'>

        {/* Título principal del panel */}
        <h1 className='text-2xl font-bold text-gray-600 oscuroTextoGris mb-6'>
          Panel de Vendedor 
        </h1>

        {/* Botones de navegación */}
        <div className='flex flex-col md:flex-row justify-center mt-8 gap-4'>

          {/* Botón para Añadir Evento */}
          <section className='flex-1'>
            <button
              className='w-full text-white bg-blue-500 p-3 px-6 shadow hover:bg-blue-700 transition font-semibold text-md flex items-center justify-center gap-2'
              onClick={() => setActiveSection(activeSection === 'addEvent' ? null : 'addEvent')}
            >
              <span className='material-symbols-outlined'>add</span>
              Crear Evento
            </button>
          </section>

          {/* Botón para Editar Evento */}
          <section className='flex-1'>
            <button
              className='w-full text-white bg-blue-500 p-3 px-6 shadow hover:bg-blue-700 transition font-semibold text-md flex items-center justify-center gap-2'
              onClick={() => setActiveSection(activeSection === 'editEvent' ? null : 'editEvent')}
            >
              <span className='material-symbols-outlined'>event</span>
              Editar Evento
            </button>
          </section>
        </div>

        <div className='mt-10'>
          {/* Mostrar secciones */}
        {activeSection === 'addEvent' && <AddEventSection />}
        {activeSection === 'editEvent' && <EditEventSection />} 
        </div>
      </div>
    </div>
  );
};

export default VendedorPanel;
