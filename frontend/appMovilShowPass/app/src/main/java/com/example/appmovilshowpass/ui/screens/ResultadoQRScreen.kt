package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilshowpass.viewmodel.ResultadoQR
import com.example.appmovilshowpass.viewmodel.TicketViewModel

/**
 * Pantalla que muestra el resultado de la validación de un código QR.
 *
 * Funcionamiento:
 * - Observa el estado resultadoQR del TicketViewModel.
 * - Según el valor del enum ResultadoQR, muestra una pantalla distinta.
 * - Cada estado se visualiza utilizando la función PantallaGenerica.
 *
 * Estados posibles:
 *  - CARGANDO: se está validando el código QR.
 *  - VALIDO: el ticket es correcto y permite el acceso al evento.
 *  - INVALIDO: el ticket fue usado anteriormente o no es válido.
 *  - ERROR: ocurrió un fallo de conexión o error inesperado.
 *  - null: no se muestra nada mientras no exista un resultado.
 *
 * Esta pantalla no recibe parámetros externos, ya que obtiene el ViewModel
 * directamente mediante viewModel() para mantener el estado entre recomposiciones.
 */
@Composable
fun ResultadoQRScreen() {
    val viewModel : TicketViewModel = viewModel()
    val resultado by viewModel.resultadoQR.collectAsState()

    when (resultado) {
        ResultadoQR.CARGANDO -> PantallaGenerica(
            icono = null,
            mensajePrincipal = "Validando código QR...",
            mensajeSecundario = null,
            color = Color.Gray,
            textoBoton = null,
            onAceptar = {}
        )

        ResultadoQR.VALIDO -> PantallaGenerica(
            icono = Icons.Default.CheckCircle,
            mensajePrincipal = "Ticket válido",
            mensajeSecundario = "Puedes acceder al evento sin problemas.",
            color = Color(0xFF2E7D32),
            textoBoton = "Aceptar",
            onAceptar = { viewModel.resetearResultadoQR() }
        )

        ResultadoQR.INVALIDO -> PantallaGenerica(
            icono = Icons.Default.Error,
            mensajePrincipal = "Ticket no válido",
            mensajeSecundario = "El código QR ya fue usado anteriormente.",
            color = Color.Red,
            textoBoton = "Aceptar",
            onAceptar = { viewModel.resetearResultadoQR() }
        )

        ResultadoQR.ERROR -> PantallaGenerica(
            icono = Icons.Default.Warning,
            mensajePrincipal = "Error al validar el ticket",
            mensajeSecundario = "Hubo un problema de conexión. Inténtalo nuevamente.",
            color = Color.Red,
            textoBoton = "Intentar de nuevo",
            onAceptar = { viewModel.resetearResultadoQR() }
        )

        null -> {} // No se muestra nada mientras no exista resultado.
    }
}




/**
 * Componente genérico utilizado para mostrar diferentes tipos de mensajes al usuario
 * dentro de una tarjeta centrada en pantalla.
 *
 * Esta función permite reutilizar una misma interfaz visual para mostrar:
 *  - Mensajes de éxito
 *  - Advertencias
 *  - Errores
 *  - Estados de carga
 *
 * Parámetros:
 * icono Imagen vectorial opcional para representar el estado visualmente.
 *              Si es null, no se muestra ningún icono.
 *
 * mensajePrincipal Texto principal que describe el estado (obligatorio).
 *
 * mensajeSecundario Texto adicional que amplía la información del estado. Este parámetro es opcional.
 *
 * color Color principal de la interfaz. Se aplica al icono, textos y botón.
 *
 * textoBoton Texto mostrado en el botón de acción. Si es null, el botón no se muestra.
 *
 * onAceptar Acción que se ejecuta al pulsar el botón. Se utiliza, por ejemplo, para reiniciar estados del ViewModel.
 *
 * Detalles visuales:
 * - Se aplica un fondo semitransparente para centrar la atención del usuario.
 * - Se muestra una tarjeta con forma redondeada centrada en la pantalla.
 * - El contenido está alineado verticalmente y centrado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGenerica(
    icono: ImageVector?,
    mensajePrincipal: String,
    mensajeSecundario: String? = null,
    color: Color,
    textoBoton: String? = null,
    onAceptar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .height(300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                icono?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = mensajePrincipal,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.Center
                )

                mensajeSecundario?.let {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                textoBoton?.let {
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onAceptar,
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = it,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}