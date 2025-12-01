package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appmovilshowpass.ui.components.Cabecera
import com.example.appmovilshowpass.ui.theme.Roboto
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFecha
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

/**
* Pantalla principal del perfil de usuario. Su comportamiento varía en función de si el usuario
* está autenticado o no mediante AuthViewModel.currentUser.
*
* Funcionalidad general:
* - Cuando hay un usuario logueado:
*      - Se muestran sus datos personales, fotografía y datos de tarjeta.
*      - Permite refrescar la información del usuario mediante un gesto “pull-to-refresh”.
*      - Muestra botones para editar el perfil o cerrar sesión.
*
* - Cuando NO hay un usuario logueado:
*      - Se muestra una pantalla introductoria que invita a iniciar sesión o registrarse.
*
* Parámetros:
* authViewModel ViewModel encargado de la sesión y persistencia del usuario.
* onLoginClick Acción a ejecutar al pulsar el botón "Iniciar sesión".
* onRegisterClick Acción para navegar a la pantalla de registro.
* onEditClick Acción al pulsar el botón que abre la pantalla de edición del usuario.
*
* Notas técnicas:
* - Utiliza PullToRefreshBox, permitiendo que el usuario actualice sus datos deslizando hacia abajo.
* - La fotografía se carga desde URL usando Coil, pero si está vacía se muestra un icono por defecto.
* - La información se presenta en Cards diferenciadas: datos personales y datos de tarjeta.
* - Incluye validación visual, formato de fechas y renovación automática de datos del usuario.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val user = authViewModel.currentUser
    val scrollState = rememberScrollState()

    // Estado interno para el indicador de refresco
    var isRefreshing by remember { mutableStateOf(false) }

    // Alcance de corrutinas para operaciones asincrónicas
    val scope = rememberCoroutineScope()

    // Formato de fecha para datos de tarjeta
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val context = LocalContext.current

    /**
     * CASO 1: El usuario está logueado.
     * Se muestra todo el perfil junto con la opción de refrescar los datos.
     */
    if (user != null) {

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true

                    authViewModel.fetchLoggedInUser(context) { success ->
                        if (!success) {
                            Toast.makeText(context, authViewModel.error, Toast.LENGTH_SHORT).show()
                        }
                        isRefreshing = false
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Cabecera("Perfil", Icons.Default.Person)

                /**
                 * Sección: Fotografía del usuario.
                 * Si el usuario tiene foto almacenada, se muestra con Coil; si no, se utiliza un icono genérico.
                 */
                if (user.foto.isNotEmpty()) {
                    AsyncImage(
                        model = construirUrlImagen(user.foto),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Avatar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(96.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Mensaje de bienvenida
                Text(
                    text = "Bienvenido, ${user.nombre}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(20.dp))

                /**
                 * Sección: Datos personales.
                 * Los campos se muestran como texto y NO son editables aquí.
                 */
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "cuenta",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Text(
                                "Datos Personales",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }

                        HorizontalDivider(
                            Modifier.padding(vertical = 8.dp),
                            DividerDefaults.Thickness
                        )

                        InfoRow("Nombre", user.nombre)
                        InfoRow("Email", user.email)
                        InfoRow("Fecha de nacimiento", formatearFecha(user.fechaNacimiento.toString()))
                        InfoRow("Rol", user.rol.toString())
                        InfoRow("Contraseña", if (user.password.isNullOrEmpty()) "—" else "••••••••")
                    }
                }

                Spacer(Modifier.height(16.dp))

                /**
                 * Sección: Datos de la tarjeta bancaria del usuario.
                 * Se muestra únicamente si existe una cuenta asociada.
                 */
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row {
                            Icon(
                                imageVector = Icons.Filled.CreditCard,
                                contentDescription = "tarjeta",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Text(
                                "Tarjeta Bancaria",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }

                        HorizontalDivider(
                            Modifier.padding(vertical = 8.dp),
                            DividerDefaults.Thickness
                        )

                        InfoRow("Titular", user.cuenta?.nombreTitular ?: "—")
                        InfoRow("Nº Tarjeta", user.cuenta?.ntarjeta ?: "—")
                        InfoRow(
                            "Fecha Caducidad",
                            user.cuenta?.fechaCaducidad?.format(formatter) ?: "—"
                        )
                        InfoRow("CVV", user.cuenta?.cvv ?: "—")
                        InfoRow(
                            "Saldo",
                            user.cuenta?.saldo?.let { String.format("%.2f€", it) } ?: "—"
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                /**
                 * Botón para acceder a la pantalla de edición del usuario.
                 */
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Editar Perfil")
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                /**
                 * Botón de cierre de sesión.
                 */
                Button(
                    onClick = { authViewModel.logout(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }

    } else {

        /**
         * CASO 2: El usuario NO está logueado.
         * Se muestra un mensaje introductorio junto con accesos a login y registro.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.Key,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                buildAnnotatedString {
                    append("Bienvenido a ")
                    withStyle(
                        SpanStyle(
                            fontFamily = Roboto,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("SHOWPASS")
                    }
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "No has iniciado sesión.\nInicia sesión o crea una cuenta para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Iniciar sesión")
                    Icon(imageVector = Icons.Outlined.Login, contentDescription = null)
                }

                FilledTonalButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Crear cuenta")
                    Icon(imageVector = Icons.Outlined.PersonAdd, contentDescription = null)
                }
            }
        }
    }
}


/**
 * Fila reutilizable para mostrar un par clave–valor dentro de las tarjetas del perfil.
 *
 * Parámetros:
 * label Texto descriptivo del dato (ejemplo: "Email", "Fecha de nacimiento").
 * value Valor que se mostrará asociado a la etiqueta.
 *
 * Esta función proporciona una estructura uniforme para la presentación de
 * los datos del usuario dentro de UsuarioScreen.
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
