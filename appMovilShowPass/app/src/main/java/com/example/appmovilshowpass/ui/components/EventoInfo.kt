package com.example.appmovilshowpass.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.utils.formatearFecha
import kotlinx.coroutines.launch

@Composable
fun EventoInfo(eventoId: Long) {
    val context = LocalContext.current
    var evento by remember { mutableStateOf<Evento?>(null) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Llamada al backend
    LaunchedEffect(eventoId) {
        scope.launch {
            try {
                val response = RetrofitClient.eventoApiService.findById(eventoId)
                evento = response.toEvento()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al cargar evento", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Contenido
    evento?.let { e ->
        Column(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 0.dp)
                .verticalScroll(scrollState)
        ) {
            if (e.imagen.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(e.imagen),
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
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                )
                {
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
                Text("Descripción", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    e.descripcion,
                    fontSize = 14.sp,
                    letterSpacing = 0.50.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Light,
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Carrusel de imágenes
                if (e.carrusels.isNotEmpty()) {
                    Text("Imágenes", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(e.carrusels) { imagenUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imagenUrl),
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
                                    .padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Foto circular
                                    Image(
                                        painter = rememberAsyncImagePainter(invitado.fotoURL),
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
                                        textAlign = TextAlign.Center
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
                            }
                        }
                    }
                    Text("Precio", fontSize = 18.sp, modifier = Modifier.padding(vertical = 6.dp))
                    Text("${e.precio} €", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(30.dp))
                    BotonesComprarTicket()
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
