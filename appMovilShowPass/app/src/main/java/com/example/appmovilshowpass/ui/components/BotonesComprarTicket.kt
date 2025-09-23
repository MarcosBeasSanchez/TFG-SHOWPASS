package com.example.appmovilshowpass.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BotonesComprarTicket() {
    val context = LocalContext.current

    // Estado interno para la cantidad
    var cantidad by remember { mutableStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (cantidad > 1) cantidad-- }, modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.DarkGray)
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Restar")
        }
        Spacer(modifier = Modifier.width(24.dp))

        Button(
            onClick = {
                Toast.makeText(
                    context,
                    "Agregaste $cantidad tickets al carrito",
                    Toast.LENGTH_SHORT
                ).show()
            },
            colors = ButtonDefaults.buttonColors(Color.DarkGray),
            modifier = Modifier
                .height(48.dp)
                .weight(1f)
        ) {
            Text(text = "Agregar al carrito: $cantidad", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(24.dp))

        IconButton(
            onClick = { cantidad++ }, modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.DarkGray)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Sumar")
        }

    }
}
