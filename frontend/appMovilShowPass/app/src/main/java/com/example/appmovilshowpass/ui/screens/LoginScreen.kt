package com.example.appmovilshowpass.ui.screens


import AuthViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Pantalla de inicio de sesión de la aplicación.
 *
 * Funcionalidad principal:
 * - Permitir al usuario introducir su correo y contraseña.
 * - Validar los campos antes de enviar la solicitud.
 * - Ejecutar el proceso de login a través del AuthViewModel.
 * - Redirigir automáticamente al usuario cuando la sesión sea válida.
 * - Mostrar mensajes de error en caso de credenciales incorrectas.
 *
 * Comportamiento reactivo:
 * - La pantalla observa cambios en `authViewModel.currentUser`.
 *   Cuando este valor deja de ser nulo, se interpreta como login correcto
 *   y se lanza la navegación mediante `onLoginSuccess`.
 *
 * authViewModel ViewModel encargado de gestionar el inicio de sesión.
 * onLoginSuccess Acción ejecutada cuando el usuario inicia sesión con éxito.
 * onGoToRegister Acción para navegar a la pantalla de registro.
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current

    // Estados internos para el formulario.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estados expuestos desde el ViewModel.
    val loading = authViewModel.loading
    val error = authViewModel.error

    /**
     * Efecto de escucha:
     * Cada vez que `currentUser` cambia, si pasa a ser distinto de null,
     * significa que el inicio de sesión ha sido exitoso.
     */
    LaunchedEffect(authViewModel.currentUser) {
        authViewModel.currentUser?.let { user ->
            Toast.makeText(context, "Bienvenido ${user.nombre}", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    /**
     * Contenedor principal de la pantalla:
     * Un formulario centrado verticalmente con campos de texto,
     * botón de envío y manejo de errores.
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        // Campo de correo electrónico.
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Campo de contraseña con ocultación del texto.
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        /**
         * Botón de enviar:
         * - Bloqueado mientras se realiza la carga.
         * - Valida campos vacíos.
         * - Llama a la función login del ViewModel.
         */
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    authViewModel.login(
                        context = context,
                        email = email.trim(),
                        password = password.trim()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Entrar")
            }
        }

        /**
         * MENSAJES DE ERROR
         *
         * Si el ViewModel establece un mensaje de error, se muestra bajo los campos.
         * En caso de error concreto de usuario no registrado,
         * se ofrece un enlace a la pantalla de registro.
         */
        error?.let { mensaje ->
            Spacer(Modifier.height(8.dp))

            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )

            if (mensaje.contains("no está registrado", ignoreCase = true)) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onGoToRegister) {
                    Text(
                        "¿No tienes cuenta? Regístrate",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}