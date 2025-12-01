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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovilshowpass.viewmodel.CarritoViewModel

/**
 * Composable que muestra los controles para seleccionar una cantidad de tickets
 * y añadirlos al carrito. Incluye botones para aumentar o disminuir la cantidad
 * y un botón principal para confirmar la operación.
 *
 * usuarioId ID del usuario que realiza la compra.
 * eventoId ID del evento cuyos tickets se van a añadir.
 * carritoViewModel ViewModel encargado de gestionar las operaciones sobre el carrito.
 * onAdded Callback que recibe la cantidad añadida al carrito, útil para mostrar mensajes de confirmación o actualizar la UI externa.
 */
@Composable
fun BotonesComprarTicket(
    usuarioId: Long,
    eventoId: Long,
    carritoViewModel: CarritoViewModel,
    onAdded: (Int) -> Unit
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
        /**
         * Botón para disminuir la cantidad de tickets.
         * No permite bajar de 1 para evitar cantidades inválidas.
         */
        IconButton(
            onClick = { if (cantidad > 1) cantidad-- },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Disminuir cantidad"
            )
        }

        /**
         * Botón principal que añade al carrito la cantidad seleccionada de tickets.
         * Se repite la operación 'cantidad' veces debido a la lógica del backend.
         * Tras añadirlos se invoca el callback onAdded().
         */
        OutlinedButton(
            onClick = {
                repeat(cantidad) {
                    carritoViewModel.agregarItem(usuarioId, eventoId)
                }
                onAdded(cantidad)
            },
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
        ) {
            Text(
                text = "Agregar al carrito: $cantidad",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }

        /**
         * Botón para incrementar la cantidad de tickets a añadir.
         */
        IconButton(
            onClick = { cantidad++ },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Aumentar cantidad"
            )
        }
    }
}