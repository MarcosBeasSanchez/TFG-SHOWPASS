import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.local.UserPreferencesKeys
import com.example.appmovilshowpass.data.local.dataStore
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioLoginBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioModificarSubida
import com.example.appmovilshowpass.data.remote.dto.toDTOsubida
import com.example.appmovilshowpass.data.remote.dto.toUsuario
import com.example.appmovilshowpass.model.Login
import com.example.appmovilshowpass.model.Register
import com.example.appmovilshowpass.model.Usuario
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel responsable de gestionar el estado de autenticación del usuario.
 * Controla el inicio de sesión, registro, actualización del perfil, carga de fotos,
 * persistencia de sesión mediante DataStore y auto-login.
 */
class AuthViewModel : ViewModel() {

    // Usuario actualmente autenticado. Es observado desde Compose.
    var currentUser by mutableStateOf<Usuario?>(null)
        private set

    // Indica si hay una operación en curso (login, auto-login, actualización...).
    var loading by mutableStateOf(false)
        private set

    // Se activa cuando se ha comprobado la sesión persistente en autoLogin().
    var isSessionChecked by mutableStateOf(false)
        private set

    // Mensaje de error para mostrar en la interfaz cuando falle alguna operación.
    var error by mutableStateOf<String?>(null)

    /**
     * Guarda el token de autenticación en DataStore.
     * Se utiliza tras un login exitoso para mantener la sesión activa.
     */
    private fun saveToken(context: Context, token: String) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[UserPreferencesKeys.USER_TOKEN] = token
            }
        }
    }

    /**
     * Recupera el token guardado en DataStore.
     * Se usa principalmente en autoLogin para validar la sesión.
     */
    private suspend fun getToken(context: Context): String? {
        return context.dataStore.data
            .map { prefs -> prefs[UserPreferencesKeys.USER_TOKEN] }
            .firstOrNull()
    }

    /**
     * Borra todos los datos de sesión y foto guardados.
     * Se usa en logout y también cuando el token es inválido o expirado.
     */
    private fun clearSession(context: Context) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs.remove(UserPreferencesKeys.USER_TOKEN)
                prefs.remove(UserPreferencesKeys.USER_PHOTO)
            }
            currentUser = null
        }
    }

    /**
     * Cierra la sesión del usuario manualmente.
     * Elimina token y foto almacenados en DataStore.
     */
    fun logout(context: Context) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs.remove(UserPreferencesKeys.USER_TOKEN)
                prefs.remove(UserPreferencesKeys.USER_PHOTO)
            }
            currentUser = null
        }
    }

    /**
     * Realiza el inicio de sesión utilizando el backend.
     * Recibe email y contraseña, guarda el token si es válido
     * y actualiza currentUser con los datos recibidos.
     */
    fun login(
        context: Context,
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        loading = true
        error = null

        viewModelScope.launch {
            try {
                val dto = RetrofitClient.usuarioApiService.login(Login(email, password))

                // El backend indica si la operación fue exitosa mediante exito.
                if (!dto.exito) {
                    throw Exception(dto.mensaje)
                }

                // Si el backend devuelve token, se guarda.
                if (dto.token.isNotEmpty()) {
                    saveToken(context, dto.token)
                }

                // Convertimos DTO -> modelo Usuario.
                var user = dto.dtousuarioBajada?.toUsuario()

                // Si la foto no viene desde el backend, intentamos recuperar la guardada localmente.
                val fotoGuardada = context.dataStore.data
                    .map { prefs -> prefs[UserPreferencesKeys.USER_PHOTO] }
                    .firstOrNull()

                if (user != null) {
                    if (user.foto.isEmpty() && !fotoGuardada.isNullOrEmpty()) {
                        user = user.copy(foto = fotoGuardada)
                    }

                    // Guardamos la foto si existe.
                    if (user.foto.isNotEmpty()) {
                        saveUserPhoto(context, user.foto)
                    }
                }

                currentUser = user
                loading = false
                onComplete(true)

            } catch (e: Exception) {
                error = e.message ?: "Error de conexión"
                loading = false
                onComplete(false)
            }
        }
    }

    /**
     * Registra un nuevo usuario.
     * Tras el registro marca currentUser con los datos devueltos por el backend.
     */
    fun register(
        nombre: String,
        email: String,
        password: String,
        fechaNacimiento: String,
        rol: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        loading = true
        error = null

        viewModelScope.launch {
            try {
                val dto = RetrofitClient.usuarioApiService.register(
                    Register(
                        nombre,
                        email,
                        password,
                        LocalDate.parse(fechaNacimiento).toString(),
                        rol
                    )
                )

                // Convertimos DTO -> Usuario.
                currentUser = dto.toUsuario()

                loading = false
                onComplete(true)

            } catch (e: Exception) {
                error = e.message ?: "Error de conexión"
                loading = false
                onComplete(false)
            }
        }
    }

    /**
     * Actualiza los datos del usuario en el backend.
     * Envía un DTO con los campos modificados y luego actualiza el estado local del ViewModel.
     */
    fun updateUser(context: Context, usuario: Usuario, onComplete: (Boolean) -> Unit = {}) {
        loading = true
        error = null

        viewModelScope.launch {
            try {
                // Usuario -> DTO para la API.
                val dto = DTOusuarioModificarSubida(
                    nombre = usuario.nombre,
                    email = usuario.email,
                    password = usuario.password,
                    fechaNacimiento = usuario.fechaNacimiento.toString(),
                    foto = usuario.foto,
                    rol = usuario.rol,
                    cuenta = usuario.cuenta?.toDTOsubida(),
                )

                // Enviamos los datos al backend.
                val updatedDto = RetrofitClient.usuarioApiService.updateUser(dto, usuario.id)

                // Actualizamos el usuario en el ViewModel.
                currentUser = updatedDto.toUsuario()

                // Guardamos la foto en DataStore si existe.
                currentUser?.foto?.let { foto ->
                    saveUserPhoto(context, foto)
                }

                loading = false
                onComplete(true)

            } catch (e: Exception) {
                error = e.message ?: "Error de conexión"
                loading = false
                onComplete(false)
            }
        }
    }

    /**
     * Descarga del backend el usuario actualmente logueado usando su ID.
     * Se utiliza para refrescar el perfil cuando ha podido cambiar en el servidor.
     */
    fun fetchLoggedInUser(context: Context, onComplete: (Boolean) -> Unit = {}) {

        val userId = currentUser?.id
        if (userId == null) {
            error = "No hay usuario logueado para actualizar."
            onComplete(false)
            return
        }

        loading = true
        error = null

        viewModelScope.launch {
            try {
                val updatedDto = RetrofitClient.usuarioApiService.getUserById(userId)
                val updatedUser = updatedDto.toUsuario()

                // Se guarda la foto en DataStore para mantenerla persistente.
                if (updatedUser.foto.isNotEmpty()) {
                    saveUserPhoto(context, updatedUser.foto)
                }

                currentUser = updatedUser
                loading = false
                onComplete(true)

            } catch (e: Exception) {
                error = "Error al recargar el perfil: ${e.message ?: "Conexión fallida"}"
                loading = false
                onComplete(false)
            }
        }
    }

    /**
     * Guarda la URL o Base64 de una foto en DataStore.
     */
    fun saveUserPhoto(context: Context, fotoUrl: String) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[UserPreferencesKeys.USER_PHOTO] = fotoUrl
            }
        }
    }

    /**
     * Carga la foto almacenada en DataStore y la aplica al usuario actualmente logueado.
     * Esto evita depender únicamente del backend para la imagen del usuario.
     */
    fun loadUserPhoto(context: Context) {
        viewModelScope.launch {
            val fotoUrl = context.dataStore.data
                .map { prefs -> prefs[UserPreferencesKeys.USER_PHOTO] }
                .firstOrNull()

            if (!fotoUrl.isNullOrEmpty()) {
                currentUser = currentUser?.copy(foto = fotoUrl)
            }
        }
    }

    /**
     * Comprueba si existe un token guardado e intenta validar la sesión con el backend.
     * Equivalente a mantener la sesión abierta entre reinicios de la aplicación.
     */
    fun autoLogin(context: Context) {
        viewModelScope.launch {
            loading = true

            try {
                val token = getToken(context)

                if (token.isNullOrEmpty()) {
                    // No hay token, así que no hay sesión activa.
                    currentUser = null
                } else {
                    // Validamos el token enviándolo al backend.
                    val response = RetrofitClient.usuarioApiService.getPerfil("Bearer $token")

                    when {
                        response.isSuccessful -> {
                            val dto = response.body()

                            if (dto != null) {
                                var user = dto.toUsuario()

                                // Recuperamos foto local si el backend no la devuelve.
                                val fotoGuardada = context.dataStore.data
                                    .map { prefs -> prefs[UserPreferencesKeys.USER_PHOTO] }
                                    .firstOrNull()

                                if (user.foto.isEmpty() && !fotoGuardada.isNullOrEmpty()) {
                                    user = user.copy(foto = fotoGuardada)
                                }

                                if (user.foto.isNotEmpty()) {
                                    saveUserPhoto(context, user.foto)
                                }

                                currentUser = user
                            } else {
                                currentUser = null
                            }
                        }

                        // Token expirado o inválido.
                        response.code() == 401 -> {
                            clearSession(context)
                        }

                        else -> {
                            clearSession(context)
                        }
                    }
                }

            } catch (e: Exception) {
            } finally {
                loading = false
                isSessionChecked = true
            }
        }
    }
}