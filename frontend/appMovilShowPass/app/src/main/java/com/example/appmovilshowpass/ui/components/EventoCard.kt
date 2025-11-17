package com.example.appmovilshowpass.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.utils.formatearFechayHora
import com.example.appmovilshowpass.utils.formatearPrecio

@Composable
fun EventoCard(evento: Evento, navController: NavController) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                navController.navigate("evento_info/${evento.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)

    )
    {
        Column(modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)) {
            if (evento.imagen.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(evento.imagen),
                    contentDescription = evento.nombre,
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxSize()
                        .height(200.dp)
                        .clip(RoundedCornerShape(0.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)) {
                Text(evento.nombre, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(evento.localizacion, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Inicio Evento: " + formatearFechayHora(evento.inicioEvento),
                    fontSize = 14.sp
                )
                Text(
                    "Final Evento: " + formatearFechayHora(evento.finEvento),
                    fontSize = 14.sp
                )
                Text(
                    "Invitados: " + evento.invitados.joinToString(", ") { it.nombre },
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = evento.categoria.name,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                    Text(
                        text = "${formatearPrecio(evento.precio)}€",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp)

                    )
                }
            }
        }
    }
}