package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOInvitadoSubida
import com.example.appmovilshowpass.data.remote.dto.DTOeventoSubida
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.utils.imagenToBase64
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.ui.components.InvitadoEditorUIEdit
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFechayHora
import com.example.appmovilshowpass.viewmodel.EventoViewModel


/**
 * Pantalla destinada exclusivamente a los usuarios con rol de vendedor. Permite
 * editar un evento ya existente en el sistema.
 *
 * Funcionalidad general:
 * - Carga la información del evento desde el backend utilizando su identificador.
 * - Muestra todos los datos editables: nombre, localización, descripción, precio,
 *   aforo, categoría, fecha y hora de inicio y fin del evento.
 * - Permite sustituir la imagen principal del evento utilizando un selector de archivos.
 * - Permite añadir nuevas imágenes al carrusel, eliminar imágenes existentes
 *   y previsualizarlas.
 * - Permite editar la lista de invitados del evento usando el mismo editor utilizado
 *   durante la creación.
 * - Gestiona imágenes en formato URL y Base64, interpretándolas automáticamente mediante SafeImage.
 * - Valida los datos introducidos y envía un DTO actualizado al ViewModel para el backend.
 *
 * Flujo de funcionamiento:
 * 1. LaunchedEffect carga el evento desde la API cuando se abre la pantalla.
 * 2. Todos los campos del evento se copian a estados locales para poder editarlos.
 * 3. El usuario puede modificar cualquier campo, cambiar imágenes o editar invitados.
 * 4. Al pulsar en Guardar Cambios, se construye un DTOeventoSubida.
 * 5. El ViewModel realiza la petición de actualización al backend.
 * 6. Se muestra un mensaje de confirmación mediante Snackbar y se regresa a la pantalla anterior.
 *
 * Restricciones:
 * - El evento debe existir y ser cargado correctamente.
 * - Si se selecciona una nueva portada, se reemplaza completamente.
 * - Las imágenes del carrusel se almacenan íntegramente en Base64 o rutas URL.
 *
 * Parámetros:
 * eventoId Identificador del evento que se desea editar.
 * authViewModel ViewModel del usuario autenticado.
 * navController Controlador de navegación.
 * eventoViewModel ViewModel con la lógica de comunicación con el backend.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorEditarEventoScreen(
    eventoId: Long,
    authViewModel: AuthViewModel,
    navController: NavController,
    eventoViewModel: EventoViewModel = viewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Host de Snackbar para mostrar mensajes de actualización o errores
    val snackbarHostState = remember { SnackbarHostState() }

    // Evento original cargado desde el backend
    var evento by remember { mutableStateOf<Evento?>(null) }

    /**
     * Carga inicial del evento.
     * Se ejecuta una sola vez cuando la pantalla recibe el eventoId.
     */
    LaunchedEffect(eventoId) {
        evento = RetrofitClient.eventoApiService.findById(eventoId).toEvento()
    }

    // Si aun no cargó, no renderizar nada más
    val e = evento ?: return

    // ====== Copia editable de los campos del evento ======
    var nombre by remember { mutableStateOf(e.nombre ?: "") }
    var localizacion by remember { mutableStateOf(e.localizacion ?: "") }
    var descripcion by remember { mutableStateOf(e.descripcion ?: "") }
    var precioTxt by remember { mutableStateOf(e.precio?.toString() ?: "") }
    var aforoTxt by remember { mutableStateOf(e.aforoMax?.toString() ?: "") }
    var inicioEvento by remember { mutableStateOf(e.inicioEvento ?: "") }
    var finEvento by remember { mutableStateOf(e.finEvento ?: "") }
    var categoria by remember { mutableStateOf(e.categoria ?: Categoria.OTROS) }

    // Imagen principal
    var portadaBase64 by remember { mutableStateOf<String?>(null) }
    var portadaUrl by remember { mutableStateOf(e.imagen ?: "") }

    /**
     * Carrusel de imágenes.
     * Se cargan inicialmente las URLs originales del evento.
     * Posteriormente pueden añadirse nuevas imágenes en Base64.
     */
    val carrusel = remember {
        mutableStateListOf<String>().apply { addAll(e.imagenesCarruselUrls) }
    }

    /**
     * Invitados asociados al evento.
     * Se convierten del modelo de lectura a DTOInvitadoSubida para edición.
     */
    val invitados = remember {
        mutableStateListOf<DTOInvitadoSubida>().apply {
            addAll(
                e.invitados.map {
                    DTOInvitadoSubida(
                        id = it.id,
                        nombre = it.nombre,
                        apellidos = it.apellidos,
                        descripcion = it.descripcion,
                        fotoURL = it.fotoURL
                    )
                }
            )
        }
    }

    // ====== Selectores de imágenes ======

    /**
     * Selector de imagen principal.
     * La imagen seleccionada se convierte a Base64 y sustituye por completo a la anterior.
     */
    val pickPortada = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            portadaBase64 = "data:image/png;base64," + imagenToBase64(context, it)
            portadaUrl = ""  // Se borra la url al usar Base64
        }
    }

    /**
     * Selector de imágenes de carrusel.
     * Se pueden seleccionar múltiples imágenes que se añaden al carrusel.
     */
    val pickCarrusel = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            carrusel.add("data:image/png;base64," + imagenToBase64(context, uri))
        }
    }

    // ====== Interfaz de usuario ======

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Editar Evento") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

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
             * Bloque visual: imagen principal del evento.
             * Utiliza SafeImage para mostrar tanto URLs remotas como Base64.
             */
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                SafeImage(
                    model = portadaBase64 ?: portadaUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Button(
                onClick = { pickPortada.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Cambiar Imagen Principal")
            }

            // ====== Información general del evento ======
            SectionTitle("Información del Evento")

            LabeledInput(nombre, { nombre = it }, "Nombre", Icons.Default.Event)
            LabeledInput(localizacion, { localizacion = it }, "Localización", Icons.Default.Place)
            LabeledInput(descripcion, { descripcion = it }, "Descripción", Icons.Default.Info)
            LabeledInput(precioTxt, { precioTxt = it }, "Precio €", Icons.Default.AttachMoney)
            LabeledInput(aforoTxt, { aforoTxt = it }, "Aforo Máximo", Icons.Default.People)

            // ====== Categoría y fechas ======
            SectionTitle("Categoría y Fechas")

            DropdownMenuCategoria(categoria) { categoria = it }

            // Selección de fecha y hora del inicio y fin del evento
            DateTimeInput(
                text = "Inicio",
                value = formatearFechayHora(inicioEvento),
                onClick = {
                    showDateTimePicker(context) { newValue -> inicioEvento = newValue }
                }
            )

            DateTimeInput(
                text = "Fin",
                value = formatearFechayHora(finEvento),
                onClick = {
                    showDateTimePicker(context) { newValue -> finEvento = newValue }
                }
            )

            // ====== Carrusel de imágenes ======
            SectionTitle("Carrusel")

            var imagenAEliminar by remember { mutableStateOf<String?>(null) }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(carrusel) { img ->

                    Box(Modifier.size(110.dp)) {

                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            SafeImage(model = img, modifier = Modifier.fillMaxSize())
                        }

                        IconButton(
                            onClick = { imagenAEliminar = img },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                                .size(26.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
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
                    text = { Text("¿Seguro que quieres eliminar esta imagen del carrusel?") },
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
                Spacer(Modifier.width(6.dp))
                Text("Agregar al Carrusel")
            }

            // Invitados
            SectionTitle("Invitados")

            InvitadoEditorUIEdit(invitados)

            /**
             * Botón final que construye el DTO y envía la actualización al backend.
             */
            Button(
                onClick = {

                    val dto = DTOeventoSubida(
                        nombre = nombre,
                        localizacion = localizacion,
                        inicioEvento = inicioEvento,
                        finEvento = finEvento,
                        descripcion = descripcion,
                        precio = precioTxt.toDouble(),
                        aforoMax = aforoTxt.toInt(),
                        categoria = categoria,
                        vendedorId = authViewModel.currentUser!!.id,
                        imagen = portadaBase64 ?: portadaUrl,
                        imagenesCarruselUrls = carrusel.toList(),
                        invitados = invitados.toList()
                    )

                    eventoViewModel.actualizarEvento(eventoId, dto) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Evento actualizado correctamente")
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}

/**
 * Componente seguro para mostrar imágenes que pueden venir en diferentes formatos:
 * - URL remota del backend
 * - Ruta relativa dentro del servidor
 * - Base64 con prefijo data:image
 * - Base64 sin prefijo
 *
 * Funcionamiento:
 * 1. Si la cadena está vacía, muestra un icono por defecto.
 * 2. Si detecta una ruta remota o relativa, la carga con Coil usando la utilidad construirUrlImagen.
 * 3. Si detecta Base64, intenta decodificarla a un array de bytes.
 *
 * Este componente evita errores comunes cuando se trabaja con imágenes mixtas durante edición.
 */
@Composable
fun SafeImage(
    model: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (model.isNullOrBlank()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.Gray)
        }
        return
    }

    val cleanModel = model
        .replace("data:image/png;base64,", "")
        .replace("data:image/jpg;base64,", "")
        .replace("data:image/jpeg;base64,", "")

    when {
        cleanModel.startsWith("/uploads/") || cleanModel.startsWith("http") ->
            Image(
                painter = rememberAsyncImagePainter(construirUrlImagen(cleanModel)),
                contentDescription = null,
                modifier = modifier,
                contentScale = contentScale
            )

        else -> {
            val decoded = try {
                Base64.decode(cleanModel, Base64.DEFAULT)
            } catch (_: Exception) { null }

            if (decoded != null) {
                Image(
                    painter = rememberAsyncImagePainter(decoded),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = contentScale
                )
            } else {
                Box(modifier, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}