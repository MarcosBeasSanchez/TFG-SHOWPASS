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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val user = authViewModel.currentUser // detecta cambios en currentUser si hay un usuario logueado o no
    val scrollState = rememberScrollState() // para scroll
    var isRefreshing by remember { mutableStateOf(false) } //Estado de refresco
    val scope = rememberCoroutineScope() //Corrutina para refresco
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Formateador para fecha de caducidad
    val context = LocalContext.current

    // Cuando hay usuario logueado
    if (user != null) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    authViewModel.fetchLoggedInUser(context) { success ->
                        // Si la recarga falla, puedes mostrar un Toast o un Snackbar
                        if (!success) {
                            Toast.makeText(context, authViewModel.error, Toast.LENGTH_SHORT).show()
                        }
                        isRefreshing = false // Detener el indicador de carga
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // habilita scroll
                    .padding(vertical = 0.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Cabecera("Perfil", Icons.Default.Person)
                // Avatar
                if (user.foto.isNotEmpty()) {
                    AsyncImage(
                        model = construirUrlImagen(user.foto),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(96.dp)
                            .padding(4.dp)
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

                Text(
                    text = "Bienvenido, ${user.nombre}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(20.dp))

                // Datos Personales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "cuenta",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp).align(Alignment.CenterVertically))
                            Text("Datos Personales", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 5.dp).align(Alignment.CenterVertically))
                        }
                        HorizontalDivider(
                            Modifier.padding(vertical = 8.dp),
                            DividerDefaults.Thickness,
                            Color.Gray
                        )
                        InfoRow("Nombre", user.nombre)
                        InfoRow("Email", user.email)
                        InfoRow("Fecha de nacimiento", formatearFecha(user.fechaNacimiento.toString()))
                        InfoRow("Rol", user.rol.toString())
                        InfoRow(
                            "Contraseña",
                            if (user.password.isNullOrEmpty()) "—" else "••••••••"
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Datos Tarjeta
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.CreditCard,
                                contentDescription = "tarjeta",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp).align(Alignment.CenterVertically))
                            Text("Tarjeta Bancaria", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 5.dp).align(Alignment.CenterVertically))
                        }
                        HorizontalDivider(
                            Modifier.padding(vertical = 8.dp),
                            DividerDefaults.Thickness,
                            Color.Gray
                        )
                        InfoRow(
                            "Titular",
                            if (user.cuenta?.nombreTitular.isNullOrEmpty()) "—" else user.cuenta.nombreTitular
                        )
                        InfoRow(
                            "Nº Tarjeta",
                            if (user.cuenta?.ntarjeta.isNullOrEmpty()) "—" else user.cuenta.ntarjeta
                        )
                        InfoRow(
                            "Fecha Caducidad",
                            if (user.cuenta?.fechaCaducidad == null) "—" else user.cuenta.fechaCaducidad.format(
                                formatter
                            )
                        )
                        InfoRow(
                            "CVV",
                            if (user.cuenta?.cvv.isNullOrEmpty()) "—" else user.cuenta.cvv
                        )
                        InfoRow(
                            "Saldo",
                            user.cuenta?.saldo?.let { String.format("%.2f€", it) }
                                ?: "—") //formato 2 decimales
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Botones de acción (editar perfil, cerrar sesión)
                OutlinedButton(
                    onClick = { onEditClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Editar Perfil")
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar Perfil",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { authViewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Logout",
                        modifier = Modifier.padding(start = 4.dp),
                        tint = Color.White
                    )
                }
            }
        }
    } else {
        // Cuando NO hay usuario logueado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono decorativo
            Icon(
                imageVector = Icons.Outlined.Key,
                contentDescription = "Usuario",
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                buildAnnotatedString {
                    append("Bienvenido a ")
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Roboto, // Especifica la familia de fuentes Roboto
                            fontWeight = FontWeight.ExtraBold // Selecciona el archivo roboto_extrabold.ttf
                        )
                    ) {
                        // Nota: No uses Text() dentro de append, solo la String.
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
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Iniciar sesión", fontWeight = FontWeight.SemiBold)
                    Icon(
                        imageVector = Icons.Outlined.Login,
                        contentDescription = "Iniciar sesión",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                FilledTonalButton(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text="Crear cuenta", fontWeight = FontWeight.SemiBold)
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = "Crear cuenta",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

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
