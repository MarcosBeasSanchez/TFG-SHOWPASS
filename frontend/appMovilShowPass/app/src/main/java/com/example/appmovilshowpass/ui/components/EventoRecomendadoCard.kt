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

            Image(
                painter = rememberAsyncImagePainter(construirUrlImagen(evento.imagen)),
                contentDescription = evento.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(10.dp)) {

                Text(
                    evento.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    evento.localizacion,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    "${formatearPrecio(evento.precio)} €",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}



@Composable
fun EventoRecomendadoCarritoCard(
    evento: DTOEventoRecomendado,
    navController: NavController
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(330.dp).clickable {
                navController.navigate("evento_info/${evento.id}")
            },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column {

            // Imagen grande con bordes redondeados arriba
            Image(
                painter = rememberAsyncImagePainter(construirUrlImagen(evento.imagen)),
                contentDescription = evento.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // Nombre del evento
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

                // Botón ver evento
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