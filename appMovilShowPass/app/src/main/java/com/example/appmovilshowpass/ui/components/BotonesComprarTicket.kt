package com.example.appmovilshowpass.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BotonesComprarTicket() {
    val context = LocalContext.current
    var cantidad by remember { mutableStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = { if (cantidad > 1) cantidad-- },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Restar")
        }

        FilledTonalButton(
            onClick = {
                Toast.makeText(
                    context,
                    "Agregaste $cantidad tickets al carrito",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .weight(1f) // ocupa to-do el espacio restante
                .height(48.dp)
        ) {
            Text(
                text = "Agregar al carrito: $cantidad",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
