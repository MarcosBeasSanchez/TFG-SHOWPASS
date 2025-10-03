package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovilshowpass.ui.screens.CarritoViewModel

@Composable
fun BotonesComprarTicket(
    usuarioId: Long,
    eventoId: Long,
    carritoViewModel: CarritoViewModel,
    onAdded: (Int) -> Unit // 👈 callback que recibe la cantidad añadida
) {
    val context = LocalContext.current
    var cantidad by remember { mutableStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Botón restar
        IconButton(
            onClick = { if (cantidad > 1) cantidad-- },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Restar")
        }

        // Botón agregar al carrito
        FilledTonalButton(
            onClick = {
                repeat(cantidad) {
                    carritoViewModel.agregarEvento(usuarioId, eventoId)
                }
                onAdded(cantidad) // 👈 se ejecuta el callback con la cantidad añadida
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Text(
                text = "Agregar al carrito: $cantidad",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Botón sumar
        IconButton(
            onClick = { cantidad++ },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Sumar")
        }
    }
}