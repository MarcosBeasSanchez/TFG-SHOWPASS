import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOtarjetaBancariaBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioLoginBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioModificarSubida
import com.example.appmovilshowpass.data.remote.dto.toUsuario
import com.example.appmovilshowpass.model.Login
import com.example.appmovilshowpass.model.Register
import com.example.appmovilshowpass.model.Usuario
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

    fun login(email: String, password: String, onComplete: (Boolean) -> Unit = {}) {
        loading = true
        error = null
        viewModelScope.launch {
            try {
                val dto: DTOusuarioLoginBajada = RetrofitClient.eventoApiService.login(Login(email, password))
                if (!dto.exito) {
                    Log.d("Login", "Error de login: ${dto.mensaje}")
                    throw Exception(dto.mensaje)
                } else {
                    Log.d("Login", "Login: ${dto.mensaje}")
                    Log.d("Usuario", "Usuario: ${dto.dtousuarioBajada}")
                    currentUser = dto.dtousuarioBajada?.toUsuario();
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

    fun register(nombre: String, email: String, password: String, fechaNacimiento: String, onComplete: (Boolean) -> Unit = {}) {
        loading = true
        error = null
        viewModelScope.launch {
            try {
                Log.d("Registro", "Registro: $nombre, $email, $password, $fechaNacimiento")
                val dto: DTOusuarioBajada = RetrofitClient.eventoApiService.register(
                    Register(
                        nombre,
                        email,
                        password,
                        LocalDate.parse(fechaNacimiento).toString()
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


    fun updateUser(usuario: Usuario, onComplete: (Boolean) -> Unit = {}) {
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
                    dtoTarjetaBancariaBajada = usuario.cuenta?.let {
                        DTOtarjetaBancariaBajada(
                            nombreTitular = it.nombreTitular,
                            nTarjeta = it.nTarjeta,
                            fechaCaducidad = it.fechaCaducidad.toString(),
                            cvv = it.cvv,
                            saldo = it.saldo
                        )
                    },
                    activo = usuario.activo
                )

                // Llamamos al endpoint
                val updatedDto = RetrofitClient.eventoApiService.updateUser(dto, usuario.id)

                // Actualizamos usuario en ViewModel
                currentUser = updatedDto.toUsuario()
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

}