package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.content.Context
import android.icu.util.Calendar
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.data.remote.dto.DTOInvitadoSubida
import com.example.appmovilshowpass.data.remote.dto.DTOeventoSubida
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.ui.components.InvitadoEditorUIEdit
import com.example.appmovilshowpass.utils.formatearFechayHora
import com.example.appmovilshowpass.utils.imagenToBase64
import com.example.appmovilshowpass.viewmodel.EventoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Pantalla utilizada por los usuarios con rol de vendedor para crear un nuevo evento.
 *
 * Funcionalidad general:
 * - Captura todos los datos necesarios para registrar un evento: nombre, localización,
 *   descripción, precio, aforo, categoría, fecha de inicio y fin.
 * - Permite seleccionar una imagen principal (portada) en formato Base64.
 * - Permite seleccionar múltiples imágenes adicionales para el carrusel.
 * - Permite añadir una lista variable de invitados, cada uno con su propia fotografía y descripción.
 * - Valida los datos mínimos necesarios antes de enviar la información.
 * - Envía el DTOeventoSubida al ViewModel para completar el proceso de alta del evento.
 *
 * Notas técnicas:
 * - Utiliza ActivityResultContracts.GetContent y GetMultipleContents para seleccionar imágenes.
 * - Todas las imágenes se convierten a Base64 antes de ser enviadas al backend.
 * - La pantalla está diseñada con desplazamiento vertical mediante verticalScroll.
 * - Utiliza SnackbarHost para mostrar mensajes de éxito o error de manera visible.
 *
 * Restricciones:
 * - Para que el evento pueda crearse debe existir al menos una imagen de portada.
 * - Los campos principales (nombre, descripción, precio, aforo) deben estar rellenos.
 *
 * Parámetros:
 * authViewModel ViewModel que contiene información del usuario autenticado.
 * navController NavController para regresar a la pantalla anterior al finalizar la creación.
 * ventoViewModel ViewModel encargado de gestionar la creación del evento en el backend.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorCrearEventoScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    eventoViewModel: EventoViewModel = viewModel()
) {
    val context = LocalContext.current
    val vendedorId = authViewModel.currentUser?.id ?: return

    // Controlador para mostrar Snackbar con mensajes de validación o éxito
    val snackbarHostState = remember { SnackbarHostState() }

    // Alcance para ejecutar corrutinas relacionadas con el envío de datos
    val scope = rememberCoroutineScope()

    // Estados de los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var localizacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTxt by remember { mutableStateOf("") }
    var aforoTxt by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.MUSICA) }

    var inicioEvento by remember { mutableStateOf("") }
    var finEvento by remember { mutableStateOf("") }

    // Imagen principal convertida en Base64
    var portadaBase64 by remember { mutableStateOf<String?>(null) }

    // Listas dinámicas de imágenes del carrusel e invitados
    val carrusel = remember { mutableStateListOf<String>() }
    val invitados = remember { mutableStateListOf<DTOInvitadoSubida>() }

    /**
     * Selectores de imágenes:
     * - pickPortada selecciona una única imagen para la portada.
     * - pickCarrusel selecciona una o varias imágenes adicionales.
     */
    val pickPortada =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { portadaBase64 = imagenToBase64(context, it) }
        }

    val pickCarrusel =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris.forEach { carrusel.add(imagenToBase64(context, it)) }
        }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Crear Evento") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        /**
         * Contenido principal de la pantalla.
         * Todo se organiza en una columna con desplazamiento vertical para permitir
         * que la pantalla sea accesible incluso cuando contiene muchos campos.
         */
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /**
             * Sección: Imagen de portada del evento.
             * Muestra una previsualización si ya se ha seleccionado una imagen.
             */
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (portadaBase64 != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            Base64.decode(portadaBase64, Base64.DEFAULT)
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            OutlinedButton(
                onClick = { pickPortada.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Seleccionar Imagen Principal")
            }

            /**
             * Sección: Información básica del evento.
             * Incluye campos como nombre, localización, descripción, precio y aforo máximo.
             */
            FieldSection("Información del Evento") {
                LabeledInput(nombre, { nombre = it }, "Nombre", Icons.Default.Event)
                LabeledInput(localizacion, { localizacion = it }, "Localización", Icons.Default.Place)
                LabeledInput(descripcion, { descripcion = it }, "Descripción", Icons.Default.Info)
                LabeledInput(precioTxt, { precioTxt = it }, "Precio €", Icons.Default.AttachMoney)
                LabeledInput(aforoTxt, { aforoTxt = it }, "Aforo Máximo", Icons.Default.People)
            }

            /**
             * Sección: Categoría.
             * Utiliza un componente desplegable para seleccionar entre las categorías enumeradas.
             */
            SectionTitle("Categoría")
            DropdownMenuCategoria(categoria) { categoria = it }

            /**
             * Sección: Fechas del evento.
             * Cada campo abre un selector combinado de fecha y hora.
             */
            FieldSection("Fecha y Hora") {
                DateTimeInput("Inicio", formatearFechayHora(inicioEvento)) {
                    showDateTimePicker(context) { inicioEvento = it }
                }
                DateTimeInput("Fin", formatearFechayHora(finEvento)) {
                    showDateTimePicker(context) { finEvento = it }
                }
            }

            /**
             * Sección: Imágenes del carrusel.
             * Permite añadir múltiples imágenes mostrando una vista previa de cada una.
             */
            FieldSection("Carrusel de imágenes") {

                var imagenAEliminar by remember { mutableStateOf<String?>(null) }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(carrusel) { img ->

                        Box(Modifier.size(110.dp)) {

                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        Base64.decode(img, Base64.DEFAULT)
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Botón para eliminar esta imagen del carrusel
                            IconButton(
                                onClick = { imagenAEliminar = img },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                                    .size(26.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }

                /**
                 * Diálogo de confirmación para eliminar una imagen del carrusel.
                 */
                if (imagenAEliminar != null) {
                    AlertDialog(
                        onDismissRequest = { imagenAEliminar = null },
                        title = { Text("Eliminar imagen") },
                        text = { Text("¿Seguro que deseas quitar esta imagen del carrusel?") },
                        confirmButton = {
                            Button(onClick = {
                                carrusel.remove(imagenAEliminar)
                                imagenAEliminar = null
                            }) { Text("Eliminar") }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { imagenAEliminar = null }) { Text("Cancelar") }
                        }
                    )
                }

                OutlinedButton(
                    onClick = { pickCarrusel.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Text("Añadir al Carrusel")
                }
            }

            /**
             * Sección de edición de invitados.
             * Utiliza el mismo componente que la pantalla de edición de eventos.
             */
            InvitadoEditorUIEdit(invitados)

            /**
             * Botón principal para crear el evento.
             * Valida los campos principales antes de enviar los datos.
             */
            Button(
                onClick = {
                    if (nombre.isBlank() || portadaBase64 == null) {
                        scope.launch { snackbarHostState.showSnackbar("Rellena todos los campos obligatorios") }
                        return@Button
                    }

                    val dto = DTOeventoSubida(
                        nombre = nombre,
                        localizacion = localizacion,
                        inicioEvento = inicioEvento,
                        finEvento = finEvento,
                        descripcion = descripcion,
                        precio = precioTxt.toDouble(),
                        categoria = categoria,
                        aforoMax = aforoTxt.toInt(),
                        vendedorId = vendedorId,
                        imagen = portadaBase64,
                        imagenesCarruselUrls = carrusel.toList(),
                        invitados = invitados.toList()
                    )

                    eventoViewModel.crearEvento(dto) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Evento creado correctamente")
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Crear Evento")
            }
        }
    }
}


// Helpers visuales reutilizables

/**
 * Sección agrupada que muestra un título y un bloque de contenido relacionado.
 * Utilizada para organizar visualmente los bloques de información del formulario.
 */
@Composable
fun FieldSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        content()
    }
}/**
 * Muestra un selector de fecha y hora combinado.
 * Primero abre un selector de fecha y, tras escogerla, un selector de hora.
 *
 * El valor final se devuelve en formato ISO estándar, compatible con el backend.
 */
fun showDateTimePicker(
    context: Context,
    onDateTimeSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()

    android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            android.app.TimePickerDialog(
                context,
                { _, hour, min ->
                    val fechaHora = LocalDateTime.of(year, month + 1, day, hour, min)
                    onDateTimeSelected(fechaHora.toString())
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

/**
 * Menú desplegable que permite seleccionar la categoría del evento.
 */
@Composable
fun DropdownMenuCategoria(
    categoriaActual: Categoria,
    onCategoriaChange: (Categoria) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(categoriaActual.name)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Categoria.entries.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.name) },
                    onClick = {
                        onCategoriaChange(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}
/**
 * Campo de texto con etiqueta e icono asociado.
 * Simplifica la creación de campos repetitivos del formulario.
 */
@Composable
fun LabeledInput(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Título de sección visual con formato más destacado.
 */
@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        textAlign = TextAlign.Start
    )
}

/**
 * Botón que muestra un valor de fecha y hora y desencadena un selector al pulsarse.
 */
@Composable
fun DateTimeInput(text: String, value: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.CalendarMonth, contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Text(if (value.isEmpty()) text else "$text: $value")
    }
}