package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appmovilshowpass.viewmodel.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    carritoViewModel: CarritoViewModel,
    usuarioId: Long,
    onCompraFinalizada: () -> Unit
) {
    val carrito = carritoViewModel.carrito.collectAsState().value
    val total = carritoViewModel.total.collectAsState().value

    // Cargar carrito al abrir
    LaunchedEffect(Unit) {
        carritoViewModel.cargarCarrito(usuarioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Tu Carrito", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = 100.dp)
                .fillMaxSize()
        ) {
            // 👉 Lista scrollable de eventos
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(carrito?.eventos ?: emptyList()) { evento ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            AsyncImage(
                                model = evento.imagen ?: "",
                                contentDescription = evento.nombre ?: "Evento",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(0.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    evento.nombre ?: "Sin nombre",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${evento.precio} €",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { carritoViewModel.eliminarEvento(usuarioId, evento.id) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // 👉 Footer fijo con total y botón pagar
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
                    Button(
                        onClick = {
                            carritoViewModel.finalizarCompra(
                                usuarioId,
                                onCompraFinalizada

                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Finalizar Compra", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}