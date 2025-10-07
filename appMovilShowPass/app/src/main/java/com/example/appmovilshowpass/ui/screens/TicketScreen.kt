package com.example.appmovilshowpass.ui.screens


import AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎟 Mis Tickets", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (tickets.isEmpty()) {
            // 🕳 Caso en el que no hay tickets
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes tickets", fontSize = 18.sp)
            }
        } else {
            // 📋 Mostrar lista de tickets
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketCard(
                            ticket = ticket,
                            onDownload = {
                                ticketViewModel.generarPdfTicket(context, ticket)
                            },
                            onSendEmail = {
                                usuario?.email?.let {
                                    ticketViewModel.enviarTicketPorEmail(context, it, ticket)
                                }
                            }
                        )
                    }
                }

                // 🗑️ Botón para vaciar tickets
                VaciarTicketsSection(
                    onConfirmar = { ticketViewModel.vaciarTickets() }
                )
            }
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
        modifier = Modifier.fillMaxWidth()
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
            Text("Fecha: ${ticket.eventoInicio.take(16)}", fontSize = 14.sp)
            Text("Precio: ${ticket.precio} €", fontSize = 14.sp)
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
                Button(
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
        Button(
            onClick = { mostrarDialogo = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Vaciar tickets", color = MaterialTheme.colorScheme.onError)
        }
    }
}