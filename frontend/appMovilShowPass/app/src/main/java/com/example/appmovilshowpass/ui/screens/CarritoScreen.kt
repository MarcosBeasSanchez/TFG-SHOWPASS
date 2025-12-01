package com.example.appmovilshowpass.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.model.CarritoItem
import com.example.appmovilshowpass.ui.components.EventoRecomendadoCarritoCard
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearPrecio
import com.example.appmovilshowpass.viewmodel.CarritoViewModel
import com.example.appmovilshowpass.viewmodel.EventoViewModel
import com.example.appmovilshowpass.viewmodel.TicketViewModel

/**
 * Pantalla principal del carrito de compra.
 *
 * Funcionalidades principales:
 * 1. Carga inicial del carrito del usuario.
 * 2. Visualización de los ítems actuales en el carrito.
 * 3. Gestión de cantidades y eliminación de entradas.
 * 4. Proceso de compra: vaciar carrito, finalizar y mostrar recomendaciones.
 * 5. Vista de carrito vacío con acción para seguir comprando.
 *
 * Esta pantalla se divide en tres escenarios:
 *  - Carrito con contenido.
 *  - Carrito vacío (antes de finalizar compra).
 *  - Carrito vacío después de finalizar compra, mostrando recomendaciones personalizadas.
 *
 * navController Controlador de navegación para abrir detalles de evento.
 * carritoViewModel ViewModel encargado de las operaciones del carrito.
 * usuarioId Identificador del usuario dueño del carrito.
 * ticketViewModel ViewModel para la generación y gestión de tickets (opcional aquí).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    usuarioId: Long,
    ticketViewModel: TicketViewModel,
) {
    // Estado reactivo del carrito y del total.
    val carrito = carritoViewModel.carrito.collectAsState().value
    val total = carritoViewModel.total.collectAsState().value

    val context = LocalContext.current

    // ViewModel para obtener recomendaciones personalizadas.
    val eventoViewModel: EventoViewModel = viewModel()
    val recomendaciones by eventoViewModel.recomendados.collectAsState()

    /**
     * True → La compra ya ha terminado: mostrar recomendaciones,
     * incluso si el carrito está vacío.
     */
    var compraFinalizada by remember { mutableStateOf(false) }

    // Cargar carrito al abrir pantalla.
    LaunchedEffect(Unit) {
        carritoViewModel.cargarCarrito(usuarioId)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        /**
         * Cabecera del carrito.
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCartCheckout,
                contentDescription = "Carrito",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Carrito de Compra",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        /**
         * ESCENARIO 1: Mostrar recomendaciones si la compra finalizó.
         */
        if (compraFinalizada && recomendaciones.isNotEmpty()) {

            Text(
                text = "Eventos que podrían interesarte",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                items(recomendaciones) { evento ->
                    EventoRecomendadoCarritoCard(
                        evento = evento,
                        navController = navController
                    )
                }
            }

            return@Column // Evitar mostrar el resto de la interfaz
        }

        /**
         * ESCENARIO 2: Carrito vacío (antes de finalizar compra).
         */
        if (carrito?.items.isNullOrEmpty() && !compraFinalizada) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Tu carrito está vacío",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AsyncImage(
                            model = "https://i.giphy.com/giXLnhxp60zEEIkq8K.webp",
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "¡Añade entradas para comenzar tu compra!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
            }

            return@Column
        }

        /**
         * ESCENARIO 3: Mostrar ítems si el carrito contiene elementos.
         */
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(carrito?.items ?: emptyList(), key = { it.id }) { item ->
                CarritoItemCard(
                    item = item,
                    onEliminar = { carritoViewModel.eliminarItem(usuarioId, item.id) }
                )
            }
        }

        /**
         * FOOTER: Total y acciones principales del carrito.
         */
        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Total actualizado automáticamente
                Text(
                    text = "Total: ${"%.2f".format(total)} €",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    /**
                     * Botón que elimina todos los ítems del carrito.
                     */
                    OutlinedButton(
                        onClick = { carritoViewModel.vaciarCarrito(usuarioId) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        border = BorderStroke(0.dp, Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Vaciar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    /**
                     * Botón que finaliza la compra:
                     * - Notifica al backend.
                     * - Limpia el carrito.
                     * - Activa el modo de recomendaciones.
                     */
                    Button(
                        onClick = {
                            carritoViewModel.finalizarCompra(usuarioId) {

                                Toast.makeText(
                                    context,
                                    " ¡Compra realizada!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                compraFinalizada = true
                                eventoViewModel.recomendarPorUsuario(usuarioId)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Icon(Icons.Outlined.ShoppingCartCheckout, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finalizar")
                    }
                }
            }
        }
    }
}


/**
 * Tarjeta que muestra un ítem dentro del carrito.
 *
 * Incluye:
 * - Imagen del evento asociado
 * - Nombre del evento
 * - Cantidad seleccionada
 * - Precio total del ítem
 * - Botón de eliminar
 *
 * item CarritoItem con la información del producto dentro del carrito.
 * onEliminar Acción que se ejecuta al pulsar el botón de eliminar.
 */
@Composable
fun CarritoItemCard(
    item: CarritoItem,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {

            // Imagen del evento si existe, sino un placeholder.
            if (!item.imagenEvento.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(construirUrlImagen(item.imagenEvento)),
                    contentDescription = item.nombreEvento,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Event, null)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombreEvento, fontWeight = FontWeight.Bold)

                Text(
                    text = "Cantidad: ${item.cantidad}",
                    color = Color.Gray
                )

                Text(
                    text = "Precio: ${formatearPrecio(item.precioUnitario * item.cantidad)} €",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}