import React, { useEffect, useState } from 'react';
import config from '../config/config';
const Profile = () => {
    const [user, setUser] = useState({
        nombre: '',
        email: '',
        fechaNacimiento: '',
        password: '',
        foto: '',
        rol: '',
        reportado: '',
        cuenta: '',
        showPassword: false
    });

    const [cuenta, setCuenta] = useState({
        id: '',
        nombreTitular: '',
        fechaCaducidad: '',
        cvv: '',
        saldo: '',
        ntarjeta: ''
    });
    const [editing, setEditing] = useState(false);
    const [editingPassword, setEditingPassword] = useState(false);

    const handleEdit = () => setEditing(true);
    const handleEditPassword = () => setEditingPassword(true);
    const handleCancel = () => setEditing(false);
    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name.startsWith('cuenta.')) {
            const field = name.split('.')[1];
            setCuenta(prev => ({
                ...prev,
                [field]: value
            }));
        } else {
            setUser(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const getImageSrc = (img) => {
        if (!img) return null; // si no hay imagen, devolvemos vacío
        if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
        if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
        if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
        return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
    };

    const handleSave = async () => {
        setEditing(false);
        setEditingPassword(false);

        const userString = localStorage.getItem("user");
        if (!userString) return;

        const userObj = JSON.parse(userString);
        const userId = userObj.id;

        // Crear el objeto de usuario a enviar
        const userToSend = {
            id: userId,
            nombre: user.nombre,
            email: user.email,
            fechaNacimiento: user.fechaNacimiento,
            foto: user.foto,
            // Solo incluir la contraseña si se está editando y tiene un valor
            ...(editingPassword && user.password && { password: user.password }),
            // INCLUIR EL OBJETO COMPLETO DE LA CUENTA BANCARIA
            // Si el backend espera un objeto anidado, lo incluimos directamente.
            cuenta: cuenta,
        };

        try {
            const responseUser = await fetch(`${config.apiBaseUrl}/tfg/usuario/update/${userId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userToSend)
            });

            if (responseUser.ok) {
                const updatedUserDTO = await responseUser.json();

                setUser(prev => ({
                    ...prev,
                    ...updatedUserDTO,
                    password: prev.password || '', // Mantener el campo password vacío si no se envió
                    // Asegurar que el objeto 'cuenta' en el estado 'user' se actualice si es necesario para otros fines.
                    cuenta: updatedUserDTO.cuenta // Esto es opcional ya que usamos el estado 'cuenta'
                }));
                setCuenta(updatedUserDTO.cuenta); // Actualizar el estado 'cuenta' con el objeto completo

                // 5. Actualizar localStorage
                const localUserToUpdate = { ...userObj, ...updatedUserDTO };
                // Asegurarse de no guardar el password en localStorage
                delete localUserToUpdate.password;
                localStorage.setItem("user", JSON.stringify(localUserToUpdate));

                alert("Usuario y Tarjeta Bancaria actualizados correctamente");

            } else {
                const errorBody = await responseUser.json();
                console.error("Error al actualizar usuario:", errorBody);
                alert(`Error actualizando datos: ${errorBody.mensaje || responseUser.statusText}`);
            }
        } catch (error) {
            console.error("Error en fetch:", error);
            alert("Ocurrió un error de conexión al guardar los datos.");
        }
    };

    useEffect(() => {
        const fetchUserAndCuenta = async () => {
            try {
                const userString = localStorage.getItem("user");
                if (!userString) return;

                const userObj = JSON.parse(userString);
                const userId = userObj.id;

                // 1.Obtener datos del usuario
                const resUser = await fetch(`${config.apiBaseUrl}/tfg/usuario/findById/${userId}`);
                const dataUser = await resUser.json();
                console.log("Usuario cargado:", dataUser);
                setUser(dataUser);

                if (dataUser.cuenta) {
                    setCuenta(dataUser.cuenta);
                }

            } catch (error) {
                console.error("Error cargando usuario o cuenta bancaria:", error);
            }
        };
        fetchUserAndCuenta();
    }, []);




    return (
        <div className="flex items-center justify-center min-h-screen min-w-full">
            <form className="max-w-3xl w-full bg-white oscuro p-8  shadow space-y-4">
                <h2 className="text-2xl font-bold text-center text-gray-500 mb-6 oscuroTextoGris">
                    PERFIL DE USUARIO
                </h2>
                <div className="flex flex-col items-center justify-center">
                    <div className="w-32 h-32 rounded-full overflow-hidden border-4 border-gray-300 mb-4 bg-gray-100 flex items-center justify-center">
                        {user.foto ? (
                            <img
                                src={getImageSrc(user.foto)}
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
                            className="w-70 p-1 border rounded-xs text-xs text-black bg-gray-100 oscuroBox"
                        />
                    )}
                </div>

                <div className="flex flex-col md:flex-row gap-8 ">
                    {/* Columna 1: Datos personales */}
                    <div className="flex-1 space-y-4">
                        <h3 className="text-xl font-semibold text-center text-gray-600 mb-2 oscuroTextoGris">Datos Personales</h3>
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Nombre:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="nombre"
                                    value={user.nombre ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 placeholder-gray-400 text-black oscuroBox "
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{user.nombre || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Email:</label>
                            <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{user.email || <span>&nbsp;</span>}</span>
                        </div>
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Fecha de nacimiento:</label>
                            {editing ? (
                                <input
                                    type="date"
                                    name="fechaNacimiento"
                                    value={user.fechaNacimiento ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black oscuroBox"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox" >{new Date(user.fechaNacimiento).toLocaleDateString("es-ES") || <span>&nbsp;</span>}</span>
                            )}
                        </div>
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris  ">Contraseña:</label>
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
                                            className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black oscuroBox   "
                                        />
                                        <div className='flex flex-col'>
                                            <button
                                                type="button"
                                                onClick={() => setUser(prev => ({ ...prev, showPassword: !prev.showPassword }))}
                                                className="ml-2 px-2 py-1 text-xs bg-gray-400  hover:bg-gray-300 text-gray-200"
                                            >
                                                {user.showPassword ? "Ocultar" : "Ver"}
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => setEditingPassword(false)}
                                                className="ml-2 px-2 py-1 text-xs text-gray-200 bg-gray-600  hover:bg-gray-300 "
                                            >
                                                X
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    <span className="text-right p-3 bg-gray-100 rounded-lg text-black oscuroBox">
                                        <button
                                            type="button"
                                            onClick={handleEditPassword}
                                            className="ml-2 px-2 py-1 text-xs text-white bg-gray-400 hover:bg-gray-600 oscuroBox"
                                        >
                                            Editar contraseña
                                        </button>
                                    </span>
                                )
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox"><span>&nbsp;</span></span>
                            )}
                        </div>
                        <div className="flex flex-col md:flex-row gap-4">
                            <div className="flex flex-col gap-2 flex-1">
                                <label className="block text-gray-700 mb-1 oscuroTextoGris">Rol:</label>
                                <span className="block w-full p-3 bg-gray-100 rounded-lg text-black oscuroBox">{user.rol || <span>&nbsp;</span>}</span>
                            </div>
                            <div className="flex flex-col gap-2 flex-1">
                                <label className="block text-gray-700 mb-1 oscuroTextoGris">Reportado:</label>
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{typeof user.reportado === "boolean" ? (user.reportado ? 'Sí' : 'No') : <span>&nbsp;</span>}</span>
                            </div>
                        </div>
                    </div>


                    {/* Columna 2: Datos de la cuenta bancaria */}
                    <div className="flex-1 space-y-4">
                        <h3 className="text-xl font-semibold text-center text-gray-600 oscuroTextoGris mb-2">Tarjeta Bancaria</h3>

                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Titular:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.nombreTitular"
                                    value={cuenta?.nombreTitular ?? ""}
                                    maxLength={255}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black oscuroBox"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.nombreTitular || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Nº Tarjeta:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.ntarjeta"
                                    value={cuenta?.ntarjeta ?? ""}
                                    maxLength={16}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black oscuroBox"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.ntarjeta || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Fecha caducidad:</label>
                            {editing ? (
                                <input
                                    type="date"
                                    name="cuenta.fechaCaducidad"
                                    value={cuenta?.fechaCaducidad ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black oscuroBox"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{new Date(cuenta?.fechaCaducidad).toLocaleDateString("es-ES") || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">CVV:</label>
                            {editing ? (
                                <input
                                    type="text"
                                    name="cuenta.cvv"
                                    value={cuenta?.cvv ?? ""}
                                    maxLength={4}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black oscuroBox"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.cvv || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Saldo:</label>
                            {/*editing ? (
                                <input
                                    type="number"
                                    name="cuenta.saldo"
                                    value={cuenta?.saldo ?? ""}
                                    onChange={handleChange}
                                    className="w-full p-3 border rounded-lg focus:outline-none focus:ring focus:border-blue-300 text-black oscuroBox"
                                />
                            ) : (
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.saldo || <span>&nbsp;</span>}</span>
                            )*/
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.saldo ?? <span>&nbsp;</span>} €</span>
                            }
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
