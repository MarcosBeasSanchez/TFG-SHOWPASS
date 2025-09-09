import React, { useState } from 'react';
import AdminUserSection from '../pages/AdminUserSection';
import AdminEventSection from '../pages/AdminEvent';
import AdminReportSection from '../pages/AdminReports';

const AdminPanel = () => {
    const [activeSection, setActiveSection] = useState(null);

    const sectionTitles = {
        user: 'Usuarios',
        event: 'Eventos',
        report: 'Reportes'
    };

    return (
        <div className='min-h-screen p-4'>
            <div className='max-w-5xl mx-auto p-6 bg-white shadow rounded oscuro'>
                <h1 className='text-2xl font-bold text-gray-600 mb-6'>Panel de Administración</h1>

                {/* Botones de navegación */}
                <div className='flex flex-col md:flex-row justify-center  mt-8'>
                    <section className="flex-1">
                        <button
                            className={`w-full text-white bg-blue-500 p-3 px-6  shadow hover:bg-blue-700 transition font-semibold text-md flex items-center justify-center gap-2 `}
                            onClick={() => setActiveSection(activeSection === 'user' ? null : 'user')}
                        >
                            <span className="material-symbols-outlined">group</span>
                            Usuarios
                        </button>
                    </section>
                    <section className="flex-1">
                        <button
                            className={`w-full text-white bg-blue-500 p-3 px-6  shadow hover:bg-blue-700 transition font-semibold text-md flex items-center justify-center gap-2`}
                            onClick={() => setActiveSection(activeSection === 'event' ? null : 'event')}
                        >
                            <span className="material-symbols-outlined">event</span>
                            Eventos
                        </button>
                    </section>
                    <section className="flex-1">
                        <button
                            className={`w-full text-white bg-blue-500 p-3 px-6  shadow hover:bg-blue-700 transition font-semibold text-md flex items-center justify-center gap-2`}
                            onClick={() => setActiveSection(activeSection === 'report' ? null : 'report')}
                        >
                            <span className="material-symbols-outlined">bar_chart</span>
                            Reportes
                        </button>
                    </section>
                </div>


                <div className='mt-10'>
                    {activeSection && (
                        <h2 className='text-xl font-bold text-gray-600 mb-4'>
                            {sectionTitles[activeSection]}
                        </h2>
                    )}

                    {/* Usuarios */}
                    {activeSection === 'user' && <AdminUserSection />}
                    {/* Eventos */}
                    {activeSection === 'event' && <AdminEventSection />}
                    {/* Reportes */}
                    {activeSection === 'report' && <AdminReportSection />}
                </div>
            </div>
        </div>
    );
};

export default AdminPanel;