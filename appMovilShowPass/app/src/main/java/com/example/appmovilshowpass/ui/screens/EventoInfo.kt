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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.ui.components.BotonesComprarTicket
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFecha
import com.example.appmovilshowpass.utils.formatearPrecio
import com.example.appmovilshowpass.viewmodel.CarritoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoInfo(
    eventoId: Long, authViewModel: AuthViewModel,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val context = LocalContext.current
    var evento by remember { mutableStateOf<Evento?>(null) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Llamada al backend
    LaunchedEffect(eventoId) {
        scope.launch {
            try {
                val response = RetrofitClient.eventoApiService.findById(eventoId)
                evento = response.toEvento()
            } catch (e: Exception) {
                e.printStackTrace()
                snackbarHostState.showSnackbar("Error al cargar evento")
            }
        }
    }

    // Contenido con Scaffold para mostrar Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        evento?.let { e ->
            Column(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp)
                    .verticalScroll(scrollState).background(MaterialTheme.colorScheme.surface)
            ) {
                if (e.imagen.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(construirUrlImagen(e.imagen)),
                        contentDescription = e.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(0.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(e.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(e.localizacion, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            "Inicio: ${formatearFecha(e.inicioEvento)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Fin: ${formatearFecha(e.finEvento)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Descripción",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        e.descripcion,
                        fontSize = 14.sp,
                        letterSpacing = 0.50.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Light,
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Carrusel de imágenes
                    if (e.imagenesCarruselUrls.isNotEmpty()) {
                        Text(
                            "Imágenes",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
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

                        Log.d("IMAGEN_EVENTO", "imagenPrincipalUrl = ${e.imagen}")
                        Log.d("IMAGEN_EVENTO_URL", "URL final = ${construirUrlImagen(e.imagen)}")
                        Log.d("IMAGEN_CARRUSEL", "iamgenesCarruselUrls: ${e.imagenesCarruselUrls[0]}")
                        Log.d("IMAGEN_CSRRUSEL_URL", "URL final = ${construirUrlImagen(e.imagenesCarruselUrls[0])}")

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Invitados
                    if (e.invitados.isNotEmpty()) {
                        Text(
                            "Invitados",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterHorizontally
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        ) {
                            items(e.invitados) { invitado ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    modifier = Modifier
                                        .width(170.dp)
                                        .height(220.dp)
                                        .padding(4.dp),
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)

                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        //  Construimos la URL final siempre
                                        val urlFinal = construirUrlImagen(invitado.fotoURL)

                                        //  Foto circular del invitado con Coil correctamente configurado
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(urlFinal)
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

                                        // Nombre
                                        Text(
                                            text = "${invitado.nombre} ${invitado.apellidos}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            lineHeight = 14.sp,
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Descripción
                                        Text(
                                            text = invitado.descripcion,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 16.sp,
                                            maxLines = 4,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                // Logs para comprobar siempre que llega y es válida
                                    Log.d("INVITADO_IMG", "ORIGINAL: ${invitado.fotoURL}")
                                }
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("Aforo máximo", fontSize = 18.sp, modifier = Modifier.padding(vertical = 6.dp))
                    Text("${(e.aforoMax)} personas", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(15.dp))

                    // Precio y botón de carrito
                    Text("Precio", fontSize = 18.sp, modifier = Modifier.padding(vertical = 6.dp))
                    Text("PVP: ${formatearPrecio(e.precio)} €", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(30.dp))


                    val userId = authViewModel.currentUser?.id

                    if (userId != null) {
                        //  Usuario logueado → mostramos botones con cantidad
                        BotonesComprarTicket(
                            usuarioId = userId,
                            eventoId = e.id,
                            carritoViewModel = carritoViewModel,
                            onAdded = { cantidad ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Agregaste $cantidad tickets al carrito")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(60.dp))
                    } else {
                        //  Usuario NO logueado → mostramos aviso
                        Button(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Debes iniciar sesión para comprar")
                                }
                            },
                            modifier = Modifier.wrapContentWidth().align(Alignment.CenterHorizontally)
                        ) {
                            Text("Inicia sesión para comprar")
                        }
                    }
                }
            } ?: run {
                // Mientras carga
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
}