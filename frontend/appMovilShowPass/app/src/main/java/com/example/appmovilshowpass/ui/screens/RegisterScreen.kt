package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appmovilshowpass.utils.formatearFecha
import com.example.appmovilshowpass.utils.formatearFechayHora
import java.time.LocalDate
import java.util.*

/**
 * Pantalla de registro de nuevos usuarios.
 *
 * Funcionalidad principal:
 * - Permite al usuario introducir sus datos personales: nombre, email, contraseña
 *   y fecha de nacimiento.
 * - Permite seleccionar un rol (CLIENTE o VENDEDOR) mediante un menú desplegable.
 * - Realiza validaciones básicas antes del envío.
 * - Registra al usuario utilizando el AuthViewModel.
 * - Tras el registro, intenta iniciar sesión automáticamente.
 *
 * Flujo de funcionamiento:
 * 1. El usuario completa los campos del formulario.
 * 2. Se validan los campos (que no estén vacíos y que el email contenga "@").
 * 3. Se ejecuta la función `authViewModel.register`.
 * 4. Si el registro es exitoso, se inicia sesión automáticamente.
 * 5. Al completar el login, se ejecuta `onRegisterSuccess`, permitiendo navegar a la aplicación.
 *
 * Elementos interactivos:
 * - Campo de fecha con DatePicker nativo de Android.
 * - Selector de roles mediante ExposedDropdownMenu.
 * - Botón para volver a la pantalla de login.
 *
 * authViewModel ViewModel encargado de la lógica de registro y autenticación.
 * onRegisterSuccess Acción ejecutada cuando el usuario se registra e inicia sesión correctamente.
 * onGoToLogin Acción que navega a la pantalla de inicio de sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val context = LocalContext.current

    /**
     * Limpieza del estado de error cuando se accede a la pantalla.
     */
    LaunchedEffect(Unit) {
        authViewModel.error = null
    }

    // Estados del formulario.
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }

    val loading = authViewModel.loading
    val error = authViewModel.error

    /**
     * Lista de roles disponibles.
     */
    val roles = listOf("CLIENTE", "VENDEDOR")

    // Estado del menú de roles.
    var expanded by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf("CLIENTE") }

    /**
     * Selector de fecha de nacimiento utilizando DatePickerDialog.
     */
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            fechaNacimiento = String.format("%04d-%02d-%02d", y, m + 1, d)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Contenedor principal del formulario.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        /**
         * Campo: Nombre completo.
         */
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        /**
         * Campo: Correo electrónico.
         */
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        /**
         * Campo: Contraseña (con ocultación del texto).
         */
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        /**
         * Campo: Fecha de nacimiento (solo lectura).
         * Se asigna mediante el DatePicker.
         */
        OutlinedTextField(
            value = formatearFecha(fechaNacimiento, "dd/MM/yyyy"),
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de nacimiento") },
            trailingIcon = {
                IconButton(onClick = { datePicker.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        /**
         * Menú desplegable para selección del rol:
         * - CLIENTE
         * - VENDEDOR
         */
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = rolSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roles.forEach { rol ->
                    DropdownMenuItem(
                        text = { Text(rol) },
                        onClick = {
                            rolSeleccionado = rol
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /**
         * Mensaje de error si existe.
         */
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        /**
         * Botón de registro:
         * - Valida campos vacíos.
         * - Valida formato básico del email.
         * - Llama a authViewModel.register.
         * - Si el registro es correcto, inicia sesión automáticamente.
         */
        Button(
            onClick = {
                if (nombre.isBlank() || email.isBlank() || password.isBlank() || fechaNacimiento.isBlank()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (!email.contains("@")) {
                    Toast.makeText(context, "Correo no válido, falta '@'", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                authViewModel.register(
                    nombre.trim(),
                    email.trim(),
                    password.trim(),
                    LocalDate.parse(fechaNacimiento).toString(),
                    rolSeleccionado
                ) { registrado ->
                    if (registrado) {
                        authViewModel.login(
                            context,
                            email.trim(),
                            password.trim()
                        ) { loginOk ->
                            if (loginOk) {
                                onRegisterSuccess()
                            } else {
                                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Registrarse")
            }
        }

        Spacer(Modifier.height(8.dp))

        /**
         * Enlace para volver a la pantalla de login.
         */
        TextButton(onClick = onGoToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Color.Gray)
        }
    }
}