package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Composable que muestra un indicador circular de carga centrado en pantalla.
 * Se utiliza para representar estados de espera mientras se realizan operaciones
 * como llamadas al backend, carga de datos o navegación interna.
 *
 * Permite personalizar la apariencia o el posicionamiento
 * del indicador desde el lugar donde se invoque el composable.
 */
@Composable
fun BarraCarga(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxSize()        // ocupa toda el área disponible
            .wrapContentSize(),   // centra el indicador dentro de ese espacio
        color = Color.Gray
    )
}
