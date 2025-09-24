package com.example.appmovilshowpass.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.model.Evento

@Composable
fun EventoCard(evento: Evento, navController: NavController) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                navController.navigate("evento_info/${evento.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(5.dp)

    )
    {
        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)) {
            Text(evento.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(evento.localizacion, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            if (evento.imagen.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(evento.imagen),
                    contentDescription = evento.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(200.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                evento.descripcion,
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                maxLines = 7,
                letterSpacing = 0.25.sp,
                lineHeight = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Precio: ${evento.precio} €",
                fontSize = 14.sp, fontStyle = FontStyle.Italic,
                modifier = Modifier.padding()
            )

        }
    }
}