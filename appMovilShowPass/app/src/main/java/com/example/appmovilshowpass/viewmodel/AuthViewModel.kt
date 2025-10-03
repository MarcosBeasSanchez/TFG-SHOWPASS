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

class AuthViewModel : ViewModel() {

    // Estado observable desde Compose
    var currentUser by mutableStateOf<Usuario?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

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
                val dto: DTOusuarioLoginBajada =
                    RetrofitClient.eventoApiService.login(Login(email, password))
                if (!dto.exito) {
                    Log.d("Login", "Error de login: ${dto.mensaje}")
                    throw Exception(dto.mensaje)
                } else {
                    Log.d("Login", "Login: ${dto.mensaje}")
                    Log.d("Usuario", "Usuario: ${dto.dtousuarioBajada}")

                    var user = dto.dtousuarioBajada?.toUsuario()

                    // 🔹 Recuperamos la foto guardada si el backend no envió nada
                    val fotoGuardada = context.dataStore.data
                        .map { prefs -> prefs[UserPreferencesKeys.USER_PHOTO] }
                        .firstOrNull()

                    if (user != null) {
                        if (user.foto.isEmpty() && !fotoGuardada.isNullOrEmpty()) {
                            user = user.copy(foto = fotoGuardada)
                        }

                        // Guardamos la foto (si existe) en DataStore
                        if (user.foto.isNotEmpty()) {
                            saveUserPhoto(context, user.foto)
                        }
                    }

                    currentUser = user
                    loading = false
                    onComplete(true)
                }
            } catch (e: Exception) {
                error = e.message ?: "Error de conexión"
                loading = false
                onComplete(false)
            }
        }
    }

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
                Log.d("Registro", "Registro: $nombre, $email, $password, $fechaNacimiento, $rol")
                val dto: DTOusuarioBajada = RetrofitClient.eventoApiService.register(
                    Register(
                        nombre,
                        email,
                        password,
                        LocalDate.parse(fechaNacimiento).toString(),
                        rol
                    )
                )
                currentUser = dto.toUsuario() //parseo dto a usuario
                loading = false
                onComplete(true)
            } catch (e: Exception) {
                Log.e("Registro", "Error en register", e)
                error = e.message ?: "Error de conexión"
                loading = false
                onComplete(false)
            }
        }
    }

    fun logout() {
        currentUser = null
    }


    fun updateUser(context: Context, usuario: Usuario, onComplete: (Boolean) -> Unit = {}) {
        loading = true
        error = null
        viewModelScope.launch {
            try {
                // Convertimos Usuario -> DTOusuarioUpdate
                val dto = DTOusuarioModificarSubida(
                    nombre = usuario.nombre,
                    email = usuario.email,
                    password = usuario.password,
                    fechaNacimiento = usuario.fechaNacimiento.toString(),
                    foto = usuario.foto,
                    rol = usuario.rol,
                    cuenta = usuario.cuenta?.toDTOsubida(),
                    activo = usuario.activo
                )

                // Llamamos al endpoint
                val updatedDto = RetrofitClient.eventoApiService.updateUser(dto, usuario.id)

                // Actualizamos usuario en ViewModel
                currentUser = updatedDto.toUsuario()

                // Guardamos la foto en DataStore
                currentUser?.foto?.let { foto ->
                    saveUserPhoto(context, foto)
                }

                loading = false
                onComplete(true)
            } catch (e: Exception) {
                Log.e("UpdateUser", "Error en updateUser", e)
                error = e.message ?: "Error de conexión"
                loading = false
                onComplete(false)
            }
        }
    }

    fun saveUserPhoto(context: Context, fotoUrl: String) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[UserPreferencesKeys.USER_PHOTO] = fotoUrl
            }
        }
    }

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


}