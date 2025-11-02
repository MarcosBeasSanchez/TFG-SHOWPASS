package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.app.DatePickerDialog
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appmovilshowpass.ui.components.Cabecera
import com.example.appmovilshowpass.utils.construirUrlImagen
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioEditScreen(
    authViewModel: AuthViewModel,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val user = authViewModel.currentUser ?: return

    var nombre by remember { mutableStateOf(user.nombre) }
    var email by remember { mutableStateOf(user.email) }
    var password by remember { mutableStateOf("") } // No mostrar la contraseña actual
    var fechaNacimiento by remember { mutableStateOf(user.fechaNacimiento.toString()) }
    var rol by remember { mutableStateOf(user.rol) }

    var nombreTitular by remember { mutableStateOf(user.cuenta?.nombreTitular ?: "") }
    var ntarjeta by remember { mutableStateOf(user.cuenta?.ntarjeta ?: "") }
    var fechaCaducidad by remember { mutableStateOf(user.cuenta?.fechaCaducidad.toString()) }
    var cvv by remember { mutableStateOf(user.cuenta?.cvv ?: "") }
    var saldo by remember { mutableStateOf(user.cuenta?.saldo ?: 0.0) }


    var isSaving by remember { mutableStateOf(false) }
    var updateFailed by remember { mutableStateOf(false) } // Estado para errores
    val snackbarHostState = remember { SnackbarHostState() }

    var foto by remember { mutableStateOf(user.foto ?: "") }
    val context = LocalContext.current


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        foto = uri?.toString() ?: ""
    }


    LaunchedEffect(updateFailed) {
        if (updateFailed) {
            snackbarHostState.showSnackbar("Error al actualizar usuario ❌")
            updateFailed = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    isSaving = true
                    val updatedUser = user.copy(
                        nombre = nombre,
                        email = email,
                        password = if (password.isNotBlank()) password else user.password, // vacio si no hay cambio
                        fechaNacimiento = LocalDate.parse(fechaNacimiento),
                        foto = foto,
                        cuenta = user.cuenta?.copy(
                            nombreTitular = nombreTitular,
                            ntarjeta = ntarjeta,
                            fechaCaducidad = LocalDate.parse(fechaCaducidad),
                            cvv = cvv,
                            saldo = saldo as BigDecimal
                        ),
                        rol = rol,

                        )
                    authViewModel.updateUser(context, updatedUser) { success ->
                        isSaving = false
                        if (success) {
                            onSaveSuccess()
                        } else {
                            updateFailed = true
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar Cambios")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(16.dp, 0.dp, 16.dp, 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- TÍTULO ---
            Cabecera("Editar Perfil", Icons.Default.PersonOutline)
            // --- FOTO DE PERFIL ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (foto != null) {
                    AsyncImage(
                        model = construirUrlImagen(foto),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .offset(x = (30).dp, y = (45).dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Cambiar foto",
                        tint = Color.White
                    )
                }
            }


            // --- Sección Datos Personales ---
            Text("Datos personales", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)

            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Password, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(), // 🔑 Aquí se oculta el texto
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    OutlinedTextField(
                        value = fechaNacimiento,
                        onValueChange = { fechaNacimiento = it },
                        label = { Text("Fecha de nacimiento (YYYY-MM-DD)") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                DatePicker(
                                    context,
                                    { fechaNacimiento = it })
                            }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha"
                                )
                            }
                        }
                    )
                }
            }

            // --- Sección Datos de Tarjeta ---
            Text("Datos de tarjeta", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nombreTitular,
                        onValueChange = { nombreTitular = it },
                        label = { Text("Nombre titular") },
                        leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ntarjeta,
                        onValueChange = {
                            if (it.length <= 16) {
                                ntarjeta = it
                            }
                        }, // Máximo 16 dígitos
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Número de tarjeta") },
                        leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fechaCaducidad,
                        onValueChange = { fechaCaducidad = it },
                        label = { Text("Fecha de caducidad (YYYY-MM-DD)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { DatePicker(context, { fechaCaducidad = it }) }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha"
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = {
                            if (it.length <= 4) {
                                cvv = it
                            }
                        }, // Máximo 4 dígitos
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("CVV") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }
        }
    }

}

fun DatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            val fecha = String.format("%04d-%02d-%02d", y, m + 1, d)
            onDateSelected(fecha)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

