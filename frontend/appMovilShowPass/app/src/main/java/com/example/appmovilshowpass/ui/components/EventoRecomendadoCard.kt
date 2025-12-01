package com.example.appmovilshowpass.ui.components
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.data.remote.dto.DTOEventoRecomendado
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearPrecio

/**
 * Composable que muestra una tarjeta vertical compacta para un evento recomendado.
 * Está diseñada para carruseles u horizontales de scroll donde se requiere una tarjeta
 * pequeña pero informativa. Muestra:
 * - Imagen del evento
 * - Nombre
 * - Localización
 * - Precio
 *
 * Al seleccionarse, navega a la pantalla de información del evento.
 *
 * evento DTO con los datos básicos del evento recomendado.
 * onClick Acción adicional opcional ejecutada tras hacer clic.
 * navController Controlador de navegación para dirigir al detalle del evento.
 */
@Composable
fun EventoRecomendadoCard(
    evento: DTOEventoRecomendado,
    onClick: (() -> Unit)? = null,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(300.dp)
            .padding(4.dp)
            .clickable {
                navController.navigate("evento_info/${evento.id}")
                onClick?.invoke()
            },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Imagen principal del evento
            Image(
                painter = rememberAsyncImagePainter(construirUrlImagen(evento.imagen)),
                contentDescription = evento.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            // Bloque inferior con la información textual
            Column(modifier = Modifier.padding(10.dp)) {

                Text(
                    text = evento.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = evento.localizacion,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "${formatearPrecio(evento.precio)} €",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}



/**
 * Composable que muestra una tarjeta ampliada de un evento recomendado,
 * pensada para aparecer dentro del carrito como sugerencia adicional.
 *
 * Contiene:
 * - Imagen destacada del evento
 * - Nombre y localización
 * - Precio en formato destacado
 * - Botón para acceder al detalle completo del evento
 *
 * Su diseño es más grande y detallado que EventoRecomendadoCard.
 *
 * evento DTO con la información del evento recomendado.
 * navController Controlador usado para navegar a la pantalla de detalles.
 */
@Composable
fun EventoRecomendadoCarritoCard(
    evento: DTOEventoRecomendado,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(330.dp)
            .clickable {
                navController.navigate("evento_info/${evento.id}")
            },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column {

            // Imagen principal recortada con bordes redondeados superiores
            Image(
                painter = rememberAsyncImagePainter(construirUrlImagen(evento.imagen)),
                contentDescription = evento.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                contentScale = ContentScale.Crop
            )

            // Información textual del evento
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = evento.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = evento.localizacion,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Precio destacado
                Text(
                    text = "${formatearPrecio(evento.precio)} €",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Botón para acceder al detalle del evento
                Button(
                    onClick = { navController.navigate("evento_info/${evento.id}") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ver evento", fontSize = 15.sp)
                }
            }
        }
    }
}