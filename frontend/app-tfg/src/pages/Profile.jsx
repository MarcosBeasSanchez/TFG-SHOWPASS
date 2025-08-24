import React, { useEffect, useState } from 'react';
import config from '../config/config';
const Profile = () => {
    const [user, setUser] = useState({ // Estado inicial del usuario
        nombre: '',
        email: '',
        fechaNacimiento: '',
        foto: '',
        rol: '',
        cuenta: {
            nombreTitular: '',
            fechaCaducidad: '',
            cvv: '',
            saldo: '',
            ntarjeta: ''
        },
        activo: false
    });

    const [editing, setEditing] = useState(false);
    const handleEdit = () => setEditing(true);

    useEffect(() => { // Cargar datos del usuario
        const fetchUser = async () => {
            const userString = localStorage.getItem("user");
            if (!userString) return;
            const userObj = JSON.parse(userString);
            const userId = userObj.id;
            const response = await fetch(`${config.apiBaseUrl}/tfg/usuario/findById/${userId}`);
            const data = await response.json();
            setUser(data); // Actualiza el estado con los datos del usuario
        };
        fetchUser();
    }, []);

    const handleChange = (e) => { // Manejar cambios en los inputs
        const { name, value } = e.target;
        if (name.startsWith('cuenta.')) {
            const field = name.split('.')[1];
            setUser(prev => ({
                ...prev,
                cuenta: {
                    ...prev.cuenta,
                    [field]: value
                }
            }));
        } else {
            setUser(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };


    const handleSave = async () => { //Guardar Datos
        setEditing(false);
        const userString = localStorage.getItem("user");
        if (!userString) return;
        const userObj = JSON.parse(userString);
        const userId = userObj.id;
        
          try {
        const response = await fetch(`${config.apiBaseUrl}/tfg/usuario/update/${userId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        });

        if (response.ok) {
            const updatedUser = await response.json(); // Obtener usuario actualizado del backend
            localStorage.setItem("user", JSON.stringify(updatedUser)); // Guardar en localStorage
            setUser(updatedUser); // Actualizar estado
            alert("Usuario actualizado correctamente");
        } else {
            console.error("Error actualizando usuario");
        }
    } catch (error) {
        console.error("Error en fetch:", error);
    }
    };

    return (
        <div className="flex items-center justify-center min-h-screen min-w-full">
            <form className="max-w-3xl w-full bg-white p-8 rounded-xl shadow space-y-4">
                <h2 className="text-2xl font-bold text-center text-gray-500 mb-6">
                    PERFIL DE USUARIO
                </h2>
                <div className="flex flex-col items-center justify-center ">
                    <div className="w-32 h-32 rounded-full overflow-hidden border-4 border-gray-300 mb-4 bg-gray-100 flex items-center justify-center">
                        {user.foto ? (
                            <img
                                src={user.foto}
                                alt="Foto de perfil"
                                className="object-cover w-full h-full"
                            />
                        ) : (
                            <span className="text-gray-400">Sin foto</span>
                        )}
                    </div>
                    {editing && (
                        <input
                            type="file"
                            accept="image/*"
                            onChange={e => {
                                const file = e.target.files[0];
                                if (file) {
                                    const reader = new FileReader();
                                    reader.onloadend = () => {
                                        setUser(prev => ({
                                            ...prev,
                                            foto: reader.result
                                        }));
                                    };
                                    reader.readAsDataURL(file);
                                }
                            }}
                            className="w-70 p-1 border rounded-xs text-xs text-black bg-gray-100"
                        />
                    )}
                </div>

                <div className="flex flex-col md:flex-row gap-15">
                    {/* Columna 1: Datos personales */}
                    <div className="flex-1 space-y-4 ">
                        <h3 className="text-xl font-semibold text-center text-gray-600 mb-2 ">Datos personales</h3>
                        <div>
                            <label className="block text-gray-700 mb-1">Nombre:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="nombre"
                                    value={user.nombre ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.nombre || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">Email:</label>
                            <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.email || <span>&nbsp;</span>}</span>
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">Fecha de nacimiento:</label>
                            {editing ? (
                                <input
                                    type="date"
                                    name="fechaNacimiento"
                                    value={user.fechaNacimiento ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.fechaNacimiento || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">Rol:</label>
                            <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.rol || <span>&nbsp;</span>}</span>
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">Cuenta Activa:</label>
                            <span className="block p-3 bg-gray-100 rounded-lg text-black">{typeof user.activo === "boolean" ? (user.activo ? 'Sí' : 'No') : <span>&nbsp;</span>}</span>
                        </div>
                    </div>
                    {/* Columna 2: Datos de la cuenta */}
                    <div className="flex-1 space-y-4">
                        <h3 className="text-xl font-semibold text-center text-gray-600 mb-2">Datos de la cuenta</h3>
                        <div>
                            <label className="block text-gray-700 mb-1">Titular:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.nombreTitular"
                                    value={user.cuenta.nombreTitular ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.cuenta.nombreTitular || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">Fecha caducidad:</label>
                            {editing ? (
                                <input
                                    type="date"
                                    name="cuenta.fechaCaducidad"
                                    value={user.cuenta.fechaCaducidad ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.cuenta.fechaCaducidad || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">CVV:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.cvv"
                                    value={user.cuenta.cvv ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.cuenta.cvv || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">Saldo:</label>
                            {editing ? (
                                <input
                                    type="number"
                                    name="cuenta.saldo"
                                    value={user.cuenta.saldo ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.cuenta.saldo || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div>
                            <label className="block text-gray-700 mb-1">Nº Tarjeta:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.ntarjeta"
                                    value={user.cuenta.ntarjeta ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.cuenta.ntarjeta || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                    </div>
                </div>
                <div className="mt-6">
                    {editing ? (
                        <button
                            type="button"
                            onClick={handleSave}
                            className="w-full bg-green-500 text-white py-3 rounded-lg hover:bg-green-600 transition"
                        >
                            Guardar Datos
                        </button>
                    ) : (
                        <button
                            type="button"
                            onClick={handleEdit}
                            className="w-full bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition"
                        >
                            Editar
                        </button>
                    )}
                </div>
            </form>
        </div>
    );
};

export default Profile;
