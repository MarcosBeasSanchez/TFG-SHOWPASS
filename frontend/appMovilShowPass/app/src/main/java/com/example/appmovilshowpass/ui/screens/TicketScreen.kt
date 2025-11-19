package com.example.appmovilshowpass.ui.screens


import AuthViewModel
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.appmovilshowpass.R
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.EstadoTicket
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFechayHora
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
    navController: NavHostController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tickets by ticketViewModel.tickets.collectAsState()
    val usuario = authViewModel.currentUser

    // Estado para controlar la dirección: true = Orden Original, false = Orden Inverso.
    var ordenOriginal by remember { mutableStateOf(true) }

    //  Cálculo de la lista a mostrar: Se invierte si ordenOriginal es false.
    val ticketsMostrados = remember(tickets, ordenOriginal) {
        if (ordenOriginal) {
            tickets // Muestra la lista en el orden que se cargó
        } else {
            tickets.reversed() // Muestra la lista con el orden invertido
        }
    }


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
                fontSize = 20.sp
            )
        }


        //  Si no hay tickets, mostrar mensaje informativo
        if (tickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {


                    Image(
                        painter = painterResource(id = R.drawable.no_tickets),
                        contentDescription = "Imagen de tickets vacíos",
                        modifier = Modifier.size(260.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¡Consigue tus entradas!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Explora eventos y vive la diversión",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { navController.navigate("eventos") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscar eventos")
                    }
                }
            }

        } else {
            //  Botón para invertir el orden de la lista
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                OutlinedButton(
                    onClick = { ordenOriginal = !ordenOriginal }, // Alterna el estado (true <-> false)
                    shape = RoundedCornerShape(8.dp)
                ) {
                    // Usamos un icono de flechas para indicar el cambio de dirección
                    Icon(
                        imageVector = Icons.Default.Sort, // Usa Download como ejemplo para una flecha hacia abajo
                        contentDescription = if (ordenOriginal) "Cambiar a orden inverso" else "Cambiar a orden original"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (ordenOriginal) "Orden: Antiguos" else "Orden: Recientes")
                }
            }


            //  Listado de tickets del usuario
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                //  Usa la lista calculada (ticketsMostrados)
                items(ticketsMostrados) { ticket ->
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

    //  Estado para el evento cargado desde el API
    var evento by remember { mutableStateOf<Evento?>(null) }

    //  Cargar el evento cuando se crea el composable
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
            //  Imagen del evento si ya se cargó
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

            //  Información básica
            Text(evento?.nombre ?: ticket.nombreEvento, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            if (evento != null) {
                Text("Inicio del evento: ${formatearFechayHora(evento!!.inicioEvento)}", fontSize = 14.sp)
                Text("Localización: ${evento!!.localizacion}", fontSize = 14.sp)
            }

            Text("Fecha de compra: ${formatearFechayHora(ticket.fechaCompra)}", fontSize = 14.sp)
            Text("Precio: ${formatearPrecio(ticket.precioPagado)} €", fontSize = 14.sp)
            //Text("Estado: ${ticket.estado}", fontSize = 14.sp)
            Text(
                text = buildAnnotatedString {
                    // 1. Etiqueta "Estado: "
                    withStyle(style = SpanStyle(fontSize = 14.sp)) {
                        append("Estado: ")
                    }
                    // 2. Determinar el color
                    val colorEstado = when (ticket.estado) {
                        EstadoTicket.VALIDO -> Color(0xFF4CAF50) // Verde
                        EstadoTicket.USADO -> Color(0xFFFF9800)  // Naranja
                        EstadoTicket.ANULADO -> Color(0xFFF44336) // Rojo
                    }
                    // 3. Aplicar el color
                    withStyle(style = SpanStyle(fontSize = 14.sp, color = colorEstado)) {
                        // Se usa .name para obtener el String del enum (VALIDO, USADO, ANULADO)
                        append(ticket.estado.name)
                    }
                }
            )

            Spacer(Modifier.height(10.dp))

            //  Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Forma rectangular común para los botones
                val buttonShape = RoundedCornerShape(8.dp)
                // Dimensiones rectangulares
                val buttonModifier = Modifier
                    .width(90.dp) // Nuevo ancho rectangular
                    .height(50.dp) // Nueva altura

                // --- Botón Descargar (Rectangular) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        modifier = buttonModifier,
                        shape = buttonShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (descargando) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Descargar",
                                modifier = Modifier.size(24.dp) // Icono más pequeño para el rectángulo
                            )
                        }
                    }
                    Text("Descargar", fontSize = 12.sp)
                }

                // --- Botón Enviar email (Rectangular) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        modifier = buttonModifier, // Aplicamos el modificador rectangular
                        shape = buttonShape, // Aplicamos la forma rectangular
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (enviando) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Enviar",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text("Enviar email", fontSize = 12.sp)
                }

                // --- Botón Eliminar (Rectangular) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedButton(
                        onClick = { mostrarDialogoEliminar = true },
                        modifier = buttonModifier, // Aplicamos el modificador rectangular
                        shape = buttonShape, // Aplicamos la forma rectangular
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(24.dp), // Icono más pequeño para el rectángulo
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Text("Eliminar", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }
            }

        }
    }

    //  Diálogo de confirmación
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