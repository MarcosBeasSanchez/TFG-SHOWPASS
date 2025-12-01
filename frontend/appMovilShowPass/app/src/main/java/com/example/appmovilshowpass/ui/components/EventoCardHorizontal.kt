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

/**
 * Composable que muestra una tarjeta horizontal con la información principal de un evento.
 * Está diseñada para listados más compactos donde se combina texto y una imagen alineada
 * de izquierda a derecha. Incluye:
 * - Nombre del evento
 * - Imagen
 * - Localización
 * - Descripción abreviada
 * - Categoría
 * - Precio
 *
 * evento Objeto con todos los datos a mostrar.
 * modifier Permite aplicar modificadores externos al llamar este componente.
 */
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

        /**
         * Cabecera de la tarjeta.
         * Muestra únicamente el nombre del evento en formato destacado.
         */
        Text(
            text = evento.nombre,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        /**
         * Sección principal con imagen + texto.
         * Organizada en una fila horizontal.
         */
        Row(
            modifier = Modifier.padding(12.dp, 0.dp, 12.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Imagen del evento en un recuadro de tamaño fijo.
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
                        alignment = Alignment.Center
                    )
                }
                Spacer(Modifier.width(20.dp))
            }

            /**
             * Columna con la información textual del evento:
             * - Localización
             * - Descripción (abreviada con máximo de 6 líneas)
             */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(evento.localizacion, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = evento.descripcion,
                    fontSize = 12.sp,
                    maxLines = 6,
                    lineHeight = 20.sp,
                    letterSpacing = 0.25.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        /**
         * Pie de la tarjeta donde se muestran etiquetas de categoría y precio,
         * alineadas en la parte inferior derecha.
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, end = 12.dp),
            contentAlignment = Alignment.BottomEnd
        ) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // Categoría del evento dentro de una etiqueta coloreada.
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

                // Precio del evento con estilo destacado.
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