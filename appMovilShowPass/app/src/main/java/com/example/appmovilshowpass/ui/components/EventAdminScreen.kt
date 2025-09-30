package com.example.appmovilshowpass.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventAdminScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    // Estado que guarda todos los eventos
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    // Estado para la barra de búsqueda
    var search by remember { mutableStateOf("") }

    // Cuando se abre la pantalla, pedimos al backend todos los eventos
    LaunchedEffect(Unit) {
        scope.launch {
            val dtoEventos = RetrofitClient.eventoApiService.obtenerTodosEventos()
            eventos = dtoEventos.map { it.toEvento() } // Convertimos DTO -> Modelo
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Eventos") },
                navigationIcon = {
                    // Botón atrás
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Campo de búsqueda
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar evento por nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Lista de eventos (filtrada por el texto de búsqueda)
            LazyColumn {
                items(eventos.filter { it.nombre.contains(search, ignoreCase = true) }) { evento ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Imagen del evento
                            AsyncImage(
                                model = evento.imagen,
                                contentDescription = "Imagen evento",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.width(16.dp))

                            // Info del evento
                            Column(Modifier.weight(1f)) {
                                Text(evento.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(evento.localizacion, style = MaterialTheme.typography.bodySmall)
                            }

                            // Botón para eliminar evento
                            IconButton(onClick = {
                                scope.launch {
                                    // Llamada al backend para eliminar el evento
                                    try {
                                        val response =
                                            RetrofitClient.eventoApiService.deleteEvento(evento.id)

                                        if (response.isSuccessful) {
                                            // Recargar eventos
                                            val dtoEventos =
                                                RetrofitClient.eventoApiService.obtenerTodosEventos()
                                            eventos = dtoEventos.map { it.toEvento() }
                                        } else {
                                            Log.e(
                                                "EventosAdminScreen",
                                                "Error al eliminar, código: ${response.code()}"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        Log.e("EventosAdminScreen", "Error al eliminar evento", e)
                                    }
                                }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar evento",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}