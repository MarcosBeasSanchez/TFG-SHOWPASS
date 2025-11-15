package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.ui.components.Cabecera
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBorrarEventosScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var search by remember { mutableStateOf("") }

    // Controla si se muestra el diálogo y cuál evento se está eliminando
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

    // Cargar todos los eventos al entrar
    LaunchedEffect(Unit) {
        scope.launch {
            val dtoEventos = RetrofitClient.eventoApiService.obtenerTodosEventos()
            eventos = dtoEventos.map { it.toEvento() }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Cabecera(texto = "Eliminar Eventos", imageVector = Icons.Default.DeleteSweep)

        // Barra de búsqueda
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar evento por nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Lista de eventos
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(eventos.filter { it.nombre.contains(search, ignoreCase = true) }) { evento ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        // Imagen del evento
                        AsyncImage(
                            model = evento.imagen,
                            contentDescription = "Imagen evento",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.width(16.dp))

                        // Info del evento
                        Column(Modifier.weight(1f)) {
                            Text(evento.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(evento.localizacion, style = MaterialTheme.typography.bodySmall)
                        }

                        // Botón eliminar
                        IconButton(
                            onClick = {
                                eventoSeleccionado = evento
                                showDeleteDialog = true
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar evento",
                                tint = Color.Red
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // 🧩 Mostrar el diálogo de confirmación
        if (showDeleteDialog && eventoSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text("Eliminar evento", color = Color.Black)
                },
                text = {
                    Text(
                        "¿Estás seguro de eliminar el evento \"${eventoSeleccionado?.nombre}\"? Esta acción no se puede deshacer.",
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    // Llamada al backend para eliminar el evento
                                    RetrofitClient.eventoApiService.deleteEvento(eventoSeleccionado!!.id)
                                    // Refrescar la lista
                                    val dtoEventos = RetrofitClient.eventoApiService.obtenerTodosEventos()
                                    eventos = dtoEventos.map { it.toEvento() }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    showDeleteDialog = false
                                    eventoSeleccionado = null
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCDD2))
                    ) {
                        Text("Eliminar", color = Color.Black)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = Color.Black)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}