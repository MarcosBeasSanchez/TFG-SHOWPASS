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

/**
 * Pantalla destinada al administrador para visualizar, buscar y eliminar eventos del sistema.
 *
 * Características principales:
 * - Carga inicial de todos los eventos mediante llamada al backend.
 * - Barra de búsqueda que filtra eventos por nombre en tiempo real.
 * - Listado de eventos con imagen, nombre y localización.
 * - Opción para eliminar un evento, mostrando un diálogo de confirmación.
 *
 * Esta pantalla solo debería estar disponible para usuarios con rol de administrador.
 *
 * onBack Acción opcional para navegar hacia atrás, si se integra en un flujo mayor.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBorrarEventosScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Lista completa de eventos obtenidos desde el backend.
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }

    // Texto introducido en la barra de búsqueda.
    var search by remember { mutableStateOf("") }

    // Controla si se muestra el diálogo de confirmación.
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Evento actualmente seleccionado para ser eliminado.
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

    /**
     * Carga inicial de todos los eventos al entrar en la pantalla.
     * Se ejecuta una sola vez gracias al uso de LaunchedEffect(Unit).
     */
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

        /**
         * Cabecera general de la pantalla con el título correspondiente.
         */
        Cabecera(texto = "Eliminar Eventos", imageVector = Icons.Default.DeleteSweep)

        /**
         * Barra de búsqueda para filtrar eventos por nombre.
         * El filtrado se realiza de forma local, sin llamar al backend.
         */
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar evento por nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        /**
         * Lista de eventos filtrada según el texto introducido.
         * Cada ítem muestra imagen, nombre, localización y un botón de eliminación.
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(
                eventos.filter { it.nombre.contains(search, ignoreCase = true) }
            ) { evento ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {

                        /**
                         * Imagen del evento.
                         */
                        AsyncImage(
                            model = evento.imagen,
                            contentDescription = "Imagen evento",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.width(16.dp))

                        /**
                         * Información básica del evento.
                         */
                        Column(Modifier.weight(1f)) {
                            Text(evento.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(evento.localizacion, style = MaterialTheme.typography.bodySmall)
                        }

                        /**
                         * Botón que inicia el proceso de eliminación del evento.
                         * Muestra un diálogo de confirmación.
                         */
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

        /**
         * Diálogo de confirmación que se muestra antes de eliminar el evento.
         * Evita eliminaciones accidentales y avisa sobre la irreversibilidad de la acción.
         */
        if (showDeleteDialog && eventoSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar evento", color = Color.Black) },
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
                                    // Eliminación del evento en el backend.
                                    RetrofitClient.eventoApiService.deleteEvento(eventoSeleccionado!!.id)

                                    // Refrescar lista tras eliminar.
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