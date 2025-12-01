package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.appmovilshowpass.R
import com.example.appmovilshowpass.ui.components.Cabecera
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFechayHora
import com.example.appmovilshowpass.viewmodel.EventoViewModel

/**
 * Pantalla destinada a los usuarios con rol de vendedor, donde se muestran
 * todos los eventos creados por el usuario autenticado.
 *
 * Funcionalidad general:
 * - Obtiene desde el backend la lista de eventos pertenecientes al vendedor.
 * - Permite refrescar automáticamente la lista cuando el usuario regresa desde
 *   la pantalla de edición, gracias al uso de savedStateHandle y un trigger de recarga.
 * - Muestra un mensaje ilustrado cuando el vendedor no tiene eventos creados.
 * - Permite navegar a la pantalla de creación de eventos.
 * - Permite navegar a la pantalla de edición de un evento concreto al pulsar sobre él.
 *
 * Flujo resumido:
 * 1. Se obtiene el id del vendedor desde el AuthViewModel.
 * 2. En la carga inicial, se solicitan los eventos del vendedor al ViewModel.
 * 3. Si el usuario regresa desde la pantalla de edición, la pantalla detecta
 * un valor booleano en savedStateHandle (refreshEventosVendedor) y vuelve a cargar los eventos.
 * 4. Si no existen eventos, se muestra un diseño informativo con una ilustración.
 * 5. Si existen, se renderiza un listado de tarjetas con imagen y datos básicos.
 *
 * Particularidades:
 * - El sistema de recarga garantiza que si el vendedor modifica un evento en la pantalla
 *   VendedorEditarEventoScreen, al volver aquí se recarga automáticamente la información.
 * - Se usa LazyColumn para mostrar el listado, optimizando el rendimiento.
 *
 * Parámetros:
 * authViewModel ViewModel que contiene al usuario autenticado.
 * navController Controlador para navegar entre pantallas.
 * eventoViewModel ViewModel responsable de obtener los eventos del vendedor.
 */
@Composable
fun VendedorMisEventosScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    eventoViewModel: EventoViewModel = viewModel()
) {

    // Identificador del vendedor autenticado; si no existe, no se renderiza nada
    val vendedorId = authViewModel.currentUser?.id ?: return

    // Lista de eventos del vendedor (estado observado desde el ViewModel)
    val eventos by eventoViewModel.eventos.collectAsState()

    /**
     * Mecanismo de recarga automática:
     * La pantalla de edición marca un valor booleano en savedStateHandle indicando que
     * se modificó algún evento. Aquí lo observamos para refrescar la lista.
     */
    val refreshTrigger = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refreshEventosVendedor", false)
        ?.collectAsState()

    // Cuando el trigger cambie a true, se vuelve a cargar la lista de eventos
    LaunchedEffect(refreshTrigger?.value) {
        if (refreshTrigger?.value == true) {
            eventoViewModel.obtenerEventosDeVendedor(vendedorId)

            // Reset del trigger para evitar recargas posteriores innecesarias
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("refreshEventosVendedor", false)
        }
    }

    // Carga inicial de los eventos
    LaunchedEffect(Unit) {
        eventoViewModel.obtenerEventosDeVendedor(vendedorId)
    }

    // Diseño principal de la pantalla
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Sección superior con título e icono
        Cabecera("Mis Eventos", Icons.Default.EventAvailable)

        // Caso en el que el vendedor no tiene eventos creados
        if (eventos.isEmpty()) {

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

                    // Ilustración
                    Image(
                        painter = painterResource(id = R.drawable.no_tickets),
                        contentDescription = "Sin eventos",
                        modifier = Modifier.size(260.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Aún no tienes eventos creados",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Crea un nuevo evento y empieza a vender tus entradas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón para navegar a la creación de un nuevo evento
                    Button(
                        onClick = { navController.navigate("vendedor_crear") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear nuevo evento")
                    }
                }
            }

        } else {
            // Caso con eventos: se listan usando LazyColumn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                items(eventos) { e ->

                    // Tarjeta individual del evento
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Navega a la pantalla de edición del evento seleccionado
                                navController.navigate("vendedor_editar_evento/${e.id}")
                            },
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {

                        // Imagen principal del evento usando Coil
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(construirUrlImagen(e.imagen))
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build()
                            ),
                            contentDescription = e.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop
                        )

                        // Información textual mínima: nombre y fecha
                        Column(modifier = Modifier.padding(12.dp)) {

                            Text(
                                e.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = formatearFechayHora(e.inicioEvento),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}