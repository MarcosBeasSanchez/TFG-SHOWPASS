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
import androidx.compose.material3.OutlinedButton
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
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFecha
import com.example.appmovilshowpass.utils.formatearPrecio
import com.example.appmovilshowpass.viewmodel.TicketViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla principal de gestión de tickets del usuario.
 *
 * Permite:
 *  - Mostrar los tickets del usuario autenticado.
 *  - Descargar cada ticket en formato PDF.
 *  - Enviar cada ticket por correo electrónico.
 *  - Vaciar todos los tickets del usuario (confirmación incluida).
 *  - Se comunica con el backend para borrar los tickets realmente de la base de datos.
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


    //  Cargar tickets automáticamente al entrar en la pantalla
    LaunchedEffect(Unit) {
        usuario?.id?.let { ticketViewModel.cargarTickets(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        //  Encabezado de la pantalla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,

                contentDescription = "Icono tickets",
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


        //  Si no hay tickets, mostrar mensaje informativo
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

            //  Listado de tickets del usuario
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
                        },
                        onDelete = { ticketViewModel.eliminarTicket(context, ticket.id) }
                    )
                }
            }

            // Sección de "Vaciar tickets" (ahora conectada al backend)
            VaciarTicketsSection(
                onConfirmar = {
                    usuario?.id?.let { userId ->
                        ticketViewModel.vaciarTickets(context, userId)
                    }
                }
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
 *  - Botones para:
 *      - Descargar PDF
 *      - Enviar por correo
 *      - Eliminar ticket individual (con confirmación)
 */
@Composable
fun TicketCard(
    ticket: DTOTicketBajada,
    onDownload: () -> Unit,
    onSendEmail: () -> Unit,
    onDelete: () -> Unit
) {
    var enviando by remember { mutableStateOf(false) }
    var descargando by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // 🔹 Estado para el evento cargado desde el API
    var evento by remember { mutableStateOf<Evento?>(null) }

    // 🔹 Cargar el evento cuando se crea el composable
    LaunchedEffect(ticket.eventoId) {
        try {
            val dtoEvento = RetrofitClient.eventoApiService.findById(ticket.eventoId)
            evento = dtoEvento.toEvento()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
            // 🔹 Imagen del evento si ya se cargó
            if (evento?.imagen?.isNotEmpty() == true) {
                Image(
                    painter = rememberAsyncImagePainter(construirUrlImagen(evento!!.imagen)),
                    contentDescription = evento!!.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(10.dp))
            }

            // 🔹 Información básica
            Text(evento?.nombre ?: ticket.nombreEvento, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            if (evento != null) {
                Text("Inicio del evento: ${formatearFecha(evento!!.inicioEvento)}", fontSize = 14.sp)
                Text("Localización: ${evento!!.localizacion}", fontSize = 14.sp)
            }

            Text("Fecha de compra: ${formatearFecha(ticket.fechaCompra)}", fontSize = 14.sp)
            Text("Precio: ${formatearPrecio(ticket.precioPagado)} €", fontSize = 14.sp)
            Text("Estado: ${ticket.estado}", fontSize = 14.sp)

            Spacer(Modifier.height(10.dp))

            // 🔹 Código QR (si existe)
            if (!ticket.codigoQR.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(construirUrlImagen(ticket.codigoQR)),
                    contentDescription = "Código QR",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.height(10.dp))
            }

            // 🔹 Botones de acción
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(45.dp)
            ) {
                // Descargar ticket
                Button(
                    onClick = {
                        if (!descargando) {
                            descargando = true
                            onDownload()
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(3000)
                                descargando = false
                            }
                        }
                    },
                    enabled = !descargando,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (descargando)
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    else
                        Text("Descargar")
                }

                // Enviar email
                Button(
                    onClick = {
                        if (!enviando) {
                            enviando = true
                            onSendEmail()
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(3000)
                                enviando = false
                            }
                        }
                    },
                    enabled = !enviando,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (enviando)
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    else
                        Text("Enviar")
                }

                // Eliminar ticket
                OutlinedButton(
                    onClick = { mostrarDialogoEliminar = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }

    // 🔹 Diálogo de confirmación
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    mostrarDialogoEliminar = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Eliminar ticket") },
            text = { Text("¿Seguro que deseas eliminar este ticket?") }
        )
    }
}

/**
 * Sección que muestra el botón "Vaciar tickets" al final de la lista.
 *
 * Incluye:
 *  - Diálogo de confirmación para evitar eliminaciones accidentales.
 *  -  Conexión directa con el backend: elimina realmente los tickets del usuario en la BD.
 *  -  Limpia la lista local tras eliminar los datos.
 */
@Composable
fun VaciarTicketsSection(onConfirmar: () -> Unit) {
    var mostrarDialogo by remember { mutableStateOf(false) }


    //  Diálogo de confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Vaciar todos los tickets?") },

            text = { Text("Esta acción eliminará todos los tickets, descargalos o envia por correo antes de eliminar.") },
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


    //  Botón de vaciado (ahora envía la petición DELETE al backend)
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