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
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.model.CarritoItem
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearPrecio
import com.example.appmovilshowpass.viewmodel.CarritoViewModel
import com.example.appmovilshowpass.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    usuarioId: Long,
    ticketViewModel: TicketViewModel,
) {
    val carrito = carritoViewModel.carrito.collectAsState().value
    val total = carritoViewModel.total.collectAsState().value
    val context = LocalContext.current

    // Cargar carrito al abrir
    LaunchedEffect(Unit) {
        carritoViewModel.cargarCarrito(usuarioId)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Título superior
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
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "Carrito de Compra",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        //  Carrito vacío
        if (carrito?.items.isNullOrEmpty()) {

            // 1. Contenedor principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Quitar padding aquí, ya que el Card ya lo aplica, o usar solo 16.dp si es necesario
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {

                //  Tarjeta con estilo elevado y moderno
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Ocupa la mayoría del ancho
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(8.dp), // Esquinas más grandes para estilo moderno
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp), // Relleno interno generoso
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //  Título de impacto (usando headline o title large)
                        Text(
                            text = "Tu carrito está vacío",
                            style = MaterialTheme.typography.titleMedium, // Título claro y robusto
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        //  Animación o Imagen (usa el GIF, pero con un buen tamaño)
                        coil.compose.AsyncImage(
                            model = "https://i.giphy.com/giXLnhxp60zEEIkq8K.webp",
                            contentDescription = "Carrito vacío animado",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(200.dp) // Tamaño óptimo para la animació
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        //  Mensaje de apoyo
                        Text(
                            text = "¡Añade entradas de eventos para empezar tu compra!",
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


        } else {
            //  Lista de ítems del carrito
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(carrito?.items ?: emptyList(),
                    key = { item -> item.id }
                ) { item ->
                    CarritoItemCard(
                        item = item,
                        onEliminar = { carritoViewModel.eliminarItem( usuarioId,item.id) }
                    )
                }
            }

            // Footer: total y botón de pagar
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
                    Text(
                        text = "Total: ${"%.2f".format(total)} €",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón "Vaciar carrito"
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
                                .height(52.dp), // altura suficiente para que el texto no se corte
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp) // mejora legibilidad
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Borrar",
                                modifier = Modifier.padding(end = 6.dp),
                                tint = MaterialTheme.colorScheme.onError
                            )
                            Text(
                                text = "Vaciar carrito",
                                color = MaterialTheme.colorScheme.onError,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Botón "Finalizar compra"
                        Button(
                            onClick = {
                                carritoViewModel.finalizarCompra(usuarioId) {
                                    Toast.makeText(context, "✅ ¡Compra realizada!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp), // misma altura
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCartCheckout,
                                contentDescription = "Finalizar",
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "Finalizar",
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}



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
            // Imagen del evento
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
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Evento sin imagen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombreEvento,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Cantidad: ${item.cantidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "Precio: ${formatearPrecio(item.precioUnitario * item.cantidad)} €",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}