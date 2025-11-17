package com.example.appmovilshowpass.ui.components

import android.R.style.Theme
import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.utils.formatearPrecio

@Composable
fun EventoCardHorizontal(evento: Evento, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            evento.nombre,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Row(
            modifier = Modifier.padding(12.dp, 0.dp, 12.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            if (evento.imagen.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(evento.imagen),
                        contentDescription = evento.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                    )
                }
                Spacer(Modifier.width(20.dp))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {

                Text(evento.localizacion, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    evento.descripcion,
                    fontSize = 12.sp,
                    maxLines = 6,
                    letterSpacing = 0.25.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Unspecified,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(0.dp))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, end = 12.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Row(

                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. TEXTO DE CATEGORÍA
                Text(
                    text = evento.categoria.name,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                )

                // 2. TEXTO DEL PRECIO
                Text(
                    text = "${formatearPrecio(evento.precio)} €",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                )
            }
        }
    }
}