
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.model.Login
import com.example.appmovilshowpass.model.Register
import com.example.appmovilshowpass.model.Usuario
import kotlinx.coroutines.launch

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
                val user: Usuario = RetrofitClient.eventoApiService.login(Login(email, password))
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

    fun register(nombre: String, email: String, password: String, fechaNacimiento: String, onComplete: (Boolean) -> Unit = {}) {
        loading = true
        error = null
        viewModelScope.launch {
            try {
                val user: Usuario = RetrofitClient.eventoApiService.register(Register(nombre, email, password, fechaNacimiento))
                // Decide si quieres auto-login tras registrar; aquí lo hago:
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

    fun logout() {
        currentUser = null
    }
}