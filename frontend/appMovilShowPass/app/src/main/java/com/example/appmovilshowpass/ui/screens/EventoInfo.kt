package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.ui.components.BotonesComprarTicket
import com.example.appmovilshowpass.ui.components.EventoRecomendadoCard
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFechayHora
import com.example.appmovilshowpass.utils.formatearPrecio
import com.example.appmovilshowpass.viewmodel.CarritoViewModel
import com.example.appmovilshowpass.viewmodel.EventoViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle de un evento.
 *
 * Funcionalidad principal:
 * - Carga el evento desde el backend mediante su ID.
 * - Muestra información completa: imagen principal, descripción, fechas,
 *   galería de imágenes, invitados y aforo.
 * - Muestra recomendaciones basadas en el evento actual.
 * - Permite añadir entradas al carrito (si el usuario está autenticado).
 * - Muestra mensajes mediante Snackbar ante errores o acciones completadas.
 *
 * Estructura general:
 * 1. Carga inicial del evento desde la API.
 * 2. Contenido dentro de un Scaffold para soportar Snackbar.
 * 3. Información principal del evento.
 * 4. Carruseles: imágenes, invitados, recomendaciones.
 * 5. Botón de compra condicionado al estado del usuario.
 *
 * eventoId Identificador único del evento a cargar.
 * authViewModel ViewModel de autenticación utilizado para comprobar el usuario actual.
 * carritoViewModel ViewModel encargado de las operaciones del carrito.
 * navController Controlador de navegación para abrir pantallas relacionadas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoInfo(
    eventoId: Long,
    authViewModel: AuthViewModel,
    carritoViewModel: CarritoViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    // Estado que contiene el evento cargado desde la API.
    var evento by remember { mutableStateOf<Evento?>(null) }

    // Control del desplazamiento vertical.
    val scrollState = rememberScrollState()

    // Scope para ejecutar corrutinas en Compose.
    val scope = rememberCoroutineScope()

    // Snackbar para mensajes informativos o de error.
    val snackbarHostState = remember { SnackbarHostState() }

    // ViewModel para obtener recomendaciones basadas en el evento actual.
    val eventoViewModel: EventoViewModel = viewModel()
    val recomendaciones by eventoViewModel.recomendados.collectAsState()


    /**
     * -- CARGA INICIAL DEL EVENTO --
     *
     * Se ejecuta solo una vez por cada ID recibido.
     * Realiza dos operaciones:
     *  1. Obtener datos completos del evento.
     *  2. Solicitar eventos recomendados.
     */
    LaunchedEffect(eventoId) {
        scope.launch {
            try {
                val response = RetrofitClient.eventoApiService.findById(eventoId)
                evento = response.toEvento()

                // Recomendaciones por evento
                eventoViewModel.recomendarPorEvento(eventoId)

            } catch (e: Exception) {
                e.printStackTrace()
                snackbarHostState.showSnackbar("Error al cargar evento")
            }
        }
    }


    /**
     * -- INTERFAZ PRINCIPAL --
     *
     * La pantalla utiliza Scaffold para poder mostrar Snackbar sin afectar
     * el resto del diseño.
     */
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        evento?.let { e ->

            /**
             * CONTENIDO PRINCIPAL
             *
             * Toda la pantalla es scrollable para permitir mostrar
             * gran cantidad de información.
             */
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                /**
                 * IMAGEN PRINCIPAL DEL EVENTO
                 */
                if (e.imagen.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(construirUrlImagen(e.imagen)),
                        contentDescription = e.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                /**
                 * INFORMACIÓN DEL EVENTO
                 */
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(e.nombre, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(e.localizacion, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Fechas de inicio y fin
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            "Inicio: ${formatearFechayHora(e.inicioEvento)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            "Fin: ${formatearFechayHora(e.finEvento)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    /**
                     * DESCRIPCIÓN DETALLADA DEL EVENTO
                     */
                    Text("Descripción", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        e.descripcion,
                        fontSize = 14.sp,
                        letterSpacing = 0.50.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    /**
                     * CARRUSEL DE IMÁGENES ADICIONALES
                     */
                    if (e.imagenesCarruselUrls.isNotEmpty()) {
                        Text("Imágenes", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(e.imagenesCarruselUrls) { imagenUrl ->
                                Image(
                                    painter = rememberAsyncImagePainter(construirUrlImagen(imagenUrl)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(width = 300.dp, height = 200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    /**
                     * INVITADOS ASOCIADOS AL EVENTO
                     */
                    if (e.invitados.isNotEmpty()) {
                        Text("Invitados", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(e.invitados) { invitado ->

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    modifier = Modifier
                                        .width(170.dp)
                                        .height(220.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        // Imagen circular del invitado
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(construirUrlImagen(invitado.fotoURL))
                                                    .crossfade(true)
                                                    .build()
                                            ),
                                            contentDescription = invitado.nombre,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "${invitado.nombre} ${invitado.apellidos}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            invitado.descripcion,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 4,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    /**
                     * AFORO DEL EVENTO
                     */
                    Text("Aforo máximo", fontSize = 18.sp)
                    Text("${e.aforoMax} personas", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(15.dp))

                    /**
                     * EVENTOS RECOMENDADOS SEGÚN EL EVENTO
                     */
                    if (recomendaciones.isNotEmpty()) {
                        Text(
                            "Eventos recomendados",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            items(recomendaciones) { rec ->
                                EventoRecomendadoCard(
                                    evento = rec,
                                    navController = navController
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(25.dp))
                    }


                    /**
                     * PRECIO Y BOTONES DE COMPRA
                     */
                    Text("Precio", fontSize = 18.sp, modifier = Modifier.padding(vertical = 6.dp))
                    Text("PVP: ${formatearPrecio(e.precio)} €", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(30.dp))

                    val userId = authViewModel.currentUser?.id

                    /**
                     * BOTÓN DE COMPRA
                     *
                     * Dos escenarios:
                     * 1. Usuario autenticado → botones para elegir cantidad y añadir al carrito.
                     * 2. Usuario no autenticado → solicitar inicio de sesión.
                     */
                    if (userId != null) {

                        BotonesComprarTicket(
                            usuarioId = userId,
                            eventoId = e.id,
                            carritoViewModel = carritoViewModel
                        ) { cantidad ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Agregaste $cantidad tickets al carrito"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(60.dp))

                    } else {

                        Button(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Debes iniciar sesión para comprar")
                                }
                            },
                            modifier = Modifier.wrapContentWidth()
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text("Inicia sesión para comprar")
                        }
                    }
                }
            }

        } ?: run {
            // Estado mientras se carga el evento
            Text(
                "Cargando...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 18.sp
            )
        }
    }
}