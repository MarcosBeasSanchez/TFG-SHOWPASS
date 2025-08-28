import React, { useEffect, useState } from 'react';
import config from '../config/config';
const Profile = () => {
    const [user, setUser] = useState({ // Estado inicial del usuario
        nombre: '',
        email: '',
        fechaNacimiento: '',
        password: '',
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
    const [editingPassword, setEditingPassword] = useState(false);
    const handleEdit = () => setEditing(true);
    const handleEditPassword = () => setEditingPassword(true);
    const handleCancel = () => setEditing(false);
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
    const handleSave = async () => {
        setEditing(false);
        const userString = localStorage.getItem("user");
        if (!userString) return;
        const userObj = JSON.parse(userString);
        const userId = userObj.id;

        // 👇 Copia el usuario actual
        const userToSend = { ...user };

        // 👇 Si la contraseña está vacía, no la mandes
        if (!userToSend.password || userToSend.password.trim() === "") {
            delete userToSend.password;
        }

        try {
            const response = await fetch(`${config.apiBaseUrl}/tfg/usuario/update/${userId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(userToSend)
            });

            if (response.ok) {
                const updatedUser = await response.json();
                localStorage.setItem("user", JSON.stringify(updatedUser));
                setUser(updatedUser);
                alert("Usuario actualizado correctamente");
            } else {
                console.error("Error actualizando usuario");
            }
        } catch (error) {
            console.error("Error en fetch:", error);
        }
    };

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




    return (
        <div className="flex items-center justify-center min-h-screen min-w-full">
            <form className="max-w-3xl w-full bg-white p-8  shadow space-y-4">
                <h2 className="text-2xl font-bold text-center text-gray-500 mb-6">
                    PERFIL DE USUARIO
                </h2>
                <div className="flex flex-col items-center justify-center">
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

                <div className="flex flex-col md:flex-row gap-8">
                    {/* Columna 1: Datos personales */}
                    <div className="flex-1 space-y-4">
                        <h3 className="text-xl font-semibold text-center text-gray-600 mb-2">Datos Personales</h3>
                        <div className="flex flex-col gap-2">
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
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1">Email:</label>
                            <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.email || <span>&nbsp;</span>}</span>
                        </div>
                        <div className="flex flex-col gap-2">
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
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1">Contraseña:</label>
                            {editing ? (
                                editingPassword ? (
                                    <div className="flex items-center">
                                        <input
                                            type={user.showPassword ? "text" : "password"}
                                            name="password"
                                            minLength={6}
                                            placeholder="Escribe tu nueva contraseña..."
                                            value={user.password ?? ""}
                                            onChange={handleChange}
                                            className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                        />
                                        <div className='flex flex-col'>
                                            <button
                                                type="button"
                                                onClick={() => setUser(prev => ({ ...prev, showPassword: !prev.showPassword }))}
                                                className="ml-2 px-2 py-1 text-xs bg-gray-400  hover:bg-gray-300"
                                            >
                                                {user.showPassword ? "Ocultar" : "Ver"}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => setEditingPassword(false)}
                                                className="ml-2 px-2 py-1 text-xs bg-gray-600  hover:bg-gray-300"
                                            >
                                                X
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <span className="text-right p-3 bg-gray-100 rounded-lg text-black">
                                        <button
                                            type="button"
                                            onClick={handleEditPassword}
                                            className="ml-2 px-2 py-1 text-xs text-white bg-gray-400 hover:bg-gray-600"
                                        >
                                            Editar contraseña
                                        </button>
                                    </span>
                                )
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black"><span>&nbsp;</span></span>
                            )}
                        </div>
                        <div className="flex flex-col md:flex-row gap-4">
                            <div className="flex flex-col gap-2 flex-1">
                                <label className="block text-gray-700 mb-1">Rol:</label>
                                <span className="block w-full p-3 bg-gray-100 rounded-lg text-black">{user.rol || <span>&nbsp;</span>}</span>
                            </div>
                            <div className="flex flex-col gap-2 flex-1">
                                <label className="block text-gray-700 mb-1">Cuenta Activa:</label>
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{typeof user.activo === "boolean" ? (user.activo ? 'Sí' : 'No') : <span>&nbsp;</span>}</span>
                            </div>
                        </div>
                    </div>

                    <div className="flex-1 space-y-4">
                        <h3 className="text-xl font-semibold text-center text-gray-600 mb-2">Tarjeta Bancaria</h3>
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1">Titular:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.nombreTitular"
                                    value={user.cuenta.nombreTitular ?? ""}
                                    maxLength={255}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.cuenta.nombreTitular || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div className="flex flex-col gap-2">
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
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1">CVV:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.cvv"
                                    value={user.cuenta.cvv ?? ""}
                                    maxLength={4}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black">{user.cuenta.cvv || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div className="flex flex-col gap-2">
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
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1">Nº Tarjeta:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.ntarjeta"
                                    value={user.cuenta.ntarjeta ?? ""}
                                    maxLength={16}
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
                        <div className="flex flex-row gap-3">
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="w-full bg-red-500 text-white py-3 rounded-lg hover:bg-red-600 transition"
                            >
                                Cancelar
                            </button>
                            <button
                                type="button"
                                onClick={handleSave}
                                className="w-full bg-green-500 text-white py-3 rounded-lg hover:bg-green-600 transition"
                            >
                                Guardar Datos
                            </button>
                        </div>
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
