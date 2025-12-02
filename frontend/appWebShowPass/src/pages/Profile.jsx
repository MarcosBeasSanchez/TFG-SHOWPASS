import { useEffect, useState } from 'react';
import config from '../config/config';


// Define el componente funcional 'Profile'.
const Profile = () => {
    // ------------------------------------------
    // 1. GESTIÓN DE ESTADOS (useState)
    // ------------------------------------------

    // Estado para almacenar los datos del usuario.
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
    // Estado para almacenar los datos de la cuenta bancaria/tarjeta.
    const [cuenta, setCuenta] = useState({
        id: '',
        nombreTitular: '',
        fechaCaducidad: '',
        cvv: '',
        saldo: '',
        ntarjeta: ''
    });

    // Estados booleanos para controlar los modos de edición.
    const [editing, setEditing] = useState(false); // Modo edición general
    const [editingPassword, setEditingPassword] = useState(false); // Modo edición contraseña

    // ------------------------------------------
    // 2. HANDLERS DE INTERACCIÓN Y LÓGICA
    // ------------------------------------------

    // Función para activar el modo de edición principal.
    const handleEdit = () => setEditing(true);
    // Función para activar el modo de edición de la contraseña.
    const handleEditPassword = () => setEditingPassword(true);
    // Función para cancelar el modo de edición principal.
    const handleCancel = () => setEditing(false);
    // Función genérica para manejar los cambios en los campos del formulario.
    const handleChange = (e) => {
        const { name, value } = e.target;
        // Comprueba si el campo pertenece al objeto 'cuenta' (ej. si el nombre es 'cuenta.ntarjeta').
        if (name.startsWith('cuenta.')) {
            const field = name.split('.')[1]; // Extrae el nombre del campo real (ej. 'ntarjeta').
            setCuenta(prev => ({ // Actualiza el estado 'cuenta' de forma inmutable.
                ...prev,
                [field]: value
            }));
        } else {
            // Si no pertenece a 'cuenta', actualiza el estado 'user'.
            setUser(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };
    // Función asíncrona para guardar los cambios del usuario y la cuenta en el backend.
    const handleSave = async () => {
        // Desactiva los modos de edición.
        setEditing(false);
        setEditingPassword(false);
        // Obtiene los datos del usuario almacenados en localStorage (usualmente para obtener el ID).
        const userString = localStorage.getItem("user");
        if (!userString) return; // Si no hay usuario en localStorage, aborta la función.
        const userObj = JSON.parse(userString);
        const userId = userObj.id;; // Extrae el ID del usuario.

        // Crear el objeto de usuario a enviar
        const userToSend = {
            id: userId,
            nombre: user.nombre,
            email: user.email,
            fechaNacimiento: user.fechaNacimiento,
            foto: user.foto,
            // Solo incluye el campo 'password' si se está editando la contraseña y se proporcionó un valor.
            ...(editingPassword && user.password && { password: user.password }),
            // Incluye el objeto completo de la cuenta bancaria/tarjeta.
            cuenta: cuenta,
        };
        try {
            // Realiza la petición al endpoint de actualización del usuario.
            const responseUser = await fetch(`${config.apiBaseUrl}/tfg/usuario/update/${userId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userToSend)
            });

            // Comprueba si la respuesta del servidor fue exitosa (código 200-299).
            if (responseUser.ok) {
                // Parsea la respuesta JSON del servidor (el DTO del usuario actualizado).
                const updatedUserDTO = await responseUser.json();

                // 1. Actualiza el estado 'user'.
                setUser(prev => ({
                    ...prev,
                    ...updatedUserDTO,
                    password: prev.password || '', // Mantener el campo password vacío si no se envió
                    // Asegurar que el objeto 'cuenta' en el estado 'user' se actualice si es necesario para otros fines.
                    cuenta: updatedUserDTO.cuenta // Esto es opcional ya que usamos el estado 'cuenta'
                }));
                // 2. Actualiza el estado 'cuenta'.
                setCuenta(updatedUserDTO.cuenta);

                // 3. Actualizar localStorage
                const localUserToUpdate = { ...userObj, ...updatedUserDTO };

                // 4. Asegurarse de no guardar el password en localStorage por seguridad.
                delete localUserToUpdate.password;
                localStorage.setItem("user", JSON.stringify(localUserToUpdate));

                // Muestra un mensaje de éxito al usuario.
                alert("Usuario y Tarjeta Bancaria actualizados correctamente");

            } else {
                // Manejo de errores si la respuesta no es exitosa.
                const errorBody = await responseUser.json();
                console.error("Error al actualizar usuario:", errorBody);
                alert(`Error actualizando datos: ${errorBody.mensaje || responseUser.statusText}`);
            }
        } catch (error) {
            // Manejo de errores de red o del fetch.
            console.error("Error en fetch:", error);
            alert("Ocurrió un error de conexión al guardar los datos.");
        }
    };

    // ------------------------------------------
    // 3. EFECTOS (useEffect)
    // ------------------------------------------
    // Hook que se ejecuta una sola vez al montar el componente para cargar los datos iniciales.
    useEffect(() => {
        const fetchUserAndCuenta = async () => {
            try {
                // Obtener el ID del usuario desde localStorage
                const userString = localStorage.getItem("user");
                if (!userString) return;
                const userObj = JSON.parse(userString);
                const userId = userObj.id;

                // Obtener datos del usuario desde el backend
                const resUser = await fetch(`${config.apiBaseUrl}/tfg/usuario/findById/${userId}`);
                const dataUser = await resUser.json();
                console.log("Usuario cargado:", dataUser);
                setUser(dataUser);

                if (dataUser.cuenta) { // Si el usuario tiene una cuenta bancaria asociada
                    setCuenta(dataUser.cuenta);
                }

            } catch (error) {
                console.error("Error cargando usuario o cuenta bancaria:", error);
            }
        };
        fetchUserAndCuenta(); // Llama a la función para cargar los datos.
    }, []); // El array vacío asegura que este efecto solo se ejecute una vez al montar el componente.


    // ------------------------------------------
    // 4. FUNCIONES AUXILIARES
    // ------------------------------------------

    // Función auxiliar para determinar la fuente (src) correcta de una imagen según su formato/ruta.
    const getImageSrc = (img) => {
        if (!img) return null; // si no hay imagen, devolvemos vacío
        if (img.startsWith("data:image/")) return img; // ya es Base64 con prefijo → no hacer nada
        if (img.startsWith("http://") || img.startsWith("https://")) return img; // es URL externa → usar tal cual
        if (img.startsWith("/uploads/")) return `${config.apiBaseUrl}${img}`; // es ruta relativa del backend
        return `data:image/png;base64,${img}`; // es Base64 crudo → agregamos el prefijo necesario
    };

    // ------------------------------------------
    // 5. RENDERIZADO (JSX)
    // ------------------------------------------
    // Contenedor principal: centra el formulario en la pantalla y ocupa el mínimo de altura y ancho.
    return (
        <div className="flex items-center justify-center min-h-screen min-w-full">
            <form className="max-w-3xl w-full bg-white oscuro p-8  shadow space-y-4">
                {/* Título del perfil de usuario */}
                <h2 className="text-2xl font-bold text-center text-gray-500 mb-6 oscuroTextoGris">
                    PERFIL DE USUARIO
                </h2>
                <div className="flex flex-col items-center justify-center">
                    <div className="w-32 h-32 rounded-full overflow-hidden border-4 border-gray-300 mb-4 bg-gray-100 flex items-center justify-center">
                        {/* Foto de perfil */}
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

                            {/* Nombre del usuario */}
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

                        {/* Email */}
                        <div className="flex flex-col gap-2">
                            <label className="block text-gray-700 mb-1 oscuroTextoGris">Email:</label>
                            <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{user.email || <span>&nbsp;</span>}</span>
                        </div>

                        {/* Fecha de nacimiento */}
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
                            {/* Contraseña */}
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
                                ) : ( // Si no está editando la contraseña, muestra el botón para editarla
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
                            ) : ( // Modo visualización normal
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox"><span>&nbsp;</span></span>
                            )}
                        </div>
                        <div className="flex flex-col md:flex-row gap-4">

                            {/* Rol del usuario */}
                            <div className="flex flex-col gap-2 flex-1">
                                <label className="block text-gray-700 mb-1 oscuroTextoGris">Rol:</label>
                                <span className="block w-full p-3 bg-gray-100 rounded-lg text-black oscuroBox">{user.rol || <span>&nbsp;</span>}</span>
                            </div>

                            {/* Reportado */}
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
                            {/* Titular de la tarjeta */}
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
                            ) : ( // Modo visualización normal
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.nombreTitular || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        {/* Nº Tarjeta */}
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
                            ) : ( // Modo visualización normal
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.ntarjeta || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        {/* Fecha caducidad */}
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
                            ) : ( // Modo visualización normal
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{new Date(cuenta?.fechaCaducidad).toLocaleDateString("es-ES") || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        {/* CVV */}
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
                            ) : ( // Modo visualización normal
                                <span className="block p-3 bg-gray-100 rounded-lg text-black oscuroBox">{cuenta?.cvv || <span>&nbsp;</span>}</span>
                            )}
                        </div>

                        {/* Saldo */}
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

                {/* Botones de acción: Editar, Guardar, Cancelar */}
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
