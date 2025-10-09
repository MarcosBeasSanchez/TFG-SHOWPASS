package com.example.appmovilshowpass.ui.screens


import AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.example.appmovilshowpass.utils.formatearFecha
import com.example.appmovilshowpass.utils.formatearPrecio
import com.example.appmovilshowpass.viewmodel.TicketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla principal de gestión de tickets del usuario.
 *
 * Permite:
 *  - Mostrar los tickets del usuario autenticado.
 *  - Descargar cada ticket en formato PDF.
 *  - Enviar cada ticket por correo electrónico.
 *  - Vaciar todos los tickets localmente (con confirmación).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tickets by ticketViewModel.tickets.collectAsState()
    val usuario = authViewModel.currentUser

    // 🚀 Cargar tickets automáticamente al entrar en la pantalla
    LaunchedEffect(usuario?.id) {
        usuario?.id?.let { ticketViewModel.cargarTickets(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = "Carrito",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "Tickets",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        if (tickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes tickets", fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tickets) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        onDownload = { ticketViewModel.generarPdfTicket(context, ticket) },
                        onSendEmail = {
                            usuario?.email?.let {
                                ticketViewModel.enviarTicketPorEmail(context, it, ticket)
                            }
                        }
                    )
                }
            }

            VaciarTicketsSection(
                onConfirmar = { ticketViewModel.vaciarTickets() }
            )
        }
    }
}


/**
 * Componente que representa visualmente un ticket individual.
 *
 * Incluye:
 *  - Imagen del evento
 *  - Nombre, fecha y precio
 *  - Botón para descargar PDF
 *  - Botón para enviar por correo (con retardo de seguridad)
 */
@Composable
fun TicketCard(
    ticket: DTOTicketBajada,
    onDownload: () -> Unit,
    onSendEmail: () -> Unit
) {
    var enviando by remember { mutableStateOf(false) }
    var descargando by remember { mutableStateOf(false) }

    // ⚙️ Permite lanzar corrutinas desde el Composable (para delays)
    val coroutineScope = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 🖼 Imagen del evento
            Image(
                painter = rememberAsyncImagePainter(ticket.eventoImagen),
                contentDescription = ticket.eventoNombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(10.dp))

            // 📅 Información básica del evento
            Text(ticket.eventoNombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Fecha: ${formatearFecha(ticket.eventoInicio)}", fontSize = 14.sp)
            Text("Precio: ${formatearPrecio(ticket.precio)} €", fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))

            // 🧩 Botones de acción: Descargar / Enviar por correo
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // --- Descargar PDF ---
                Button(
                    onClick = {
                        if (!descargando) {
                            descargando = true
                            onDownload()
                            // ⏳ Espera 3 segundos antes de poder volver a pulsar
                            coroutineScope.launch {
                                delay(3000)
                                descargando = false
                            }
                        }
                    },
                    enabled = !descargando,
                    modifier = Modifier.weight(1f)
                ) {
                    if (descargando) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Descargar")
                    }
                }

                // --- Enviar por correo ---
                ElevatedButton(
                    onClick = {
                        if (!enviando) {
                            enviando = true
                            onSendEmail()
                            // ⏳ Espera 5 segundos antes de volver a habilitar
                            coroutineScope.launch {
                                delay(5000)
                                enviando = false
                            }
                        }
                    },
                    enabled = !enviando,
                    modifier = Modifier.weight(1f),

                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(Icons.Default.Email, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Enviar correo")
                    }
                }
            }
        }
    }
}

/**
 * Sección que muestra el botón "Vaciar tickets" al final de la lista.
 *
 * Incluye un diálogo de confirmación para evitar eliminaciones accidentales.
 */
@Composable
fun VaciarTicketsSection(onConfirmar: () -> Unit) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    // 🪟 Diálogo de confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Vaciar todos los tickets?") },
            text = { Text("Esta acción eliminará todos los tickets de tu lista local. ¿Estás seguro?") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirmar()
                    mostrarDialogo = false
                }) {
                    Text("Sí, vaciar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // 🔘 Botón de vaciado
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        FilledTonalButton(
            onClick = { mostrarDialogo = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Vaciar tickets", color = MaterialTheme.colorScheme.onError)
            Icon(
                imageVector = Icons.Outlined.DeleteForever,
                contentDescription = "Borrar",
                modifier = Modifier.padding(start = 4.dp),
                tint = MaterialTheme.colorScheme.onError
            )

        }
    }
}