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
    val snackbarHostState = remember { SnackbarHostState() }

    var evento by remember { mutableStateOf<Evento?>(null) }

    // Se carga solo una vez
    LaunchedEffect(eventoId) {
        evento = RetrofitClient.eventoApiService.findById(eventoId).toEvento()
    }
    val e = evento ?: return

    // ====== Estados editables ======
    var nombre by remember { mutableStateOf(e.nombre ?: "") }
    var localizacion by remember { mutableStateOf(e.localizacion ?: "") }
    var descripcion by remember { mutableStateOf(e.descripcion ?: "") }
    var precioTxt by remember { mutableStateOf(e.precio?.toString() ?: "") }
    var aforoTxt by remember { mutableStateOf(e.aforoMax?.toString() ?: "") }
    var inicioEvento by remember { mutableStateOf(e.inicioEvento ?: "") }
    var finEvento by remember { mutableStateOf(e.finEvento ?: "") }
    var categoria by remember { mutableStateOf(e.categoria ?: Categoria.OTROS) }

    var portadaBase64 by remember { mutableStateOf<String?>(null) }
    var portadaUrl by remember { mutableStateOf(e.imagen ?: "") }

    val carrusel = remember {
        mutableStateListOf<String>().apply { addAll(e.imagenesCarruselUrls) }
    }
    val invitados = remember {
        mutableStateListOf<DTOInvitadoSubida>().apply {
            addAll(e.invitados.map {
                DTOInvitadoSubida(
                    id = it.id,
                    nombre = it.nombre,
                    apellidos = it.apellidos,
                    descripcion = it.descripcion,
                    fotoURL = it.fotoURL
                )
            })
        }
    }

    // ====== Launchers ======
    val pickPortada = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            portadaBase64 = "data:image/png;base64," + imagenToBase64(context, it)
            portadaUrl = ""
        }
    }

    val pickCarrusel = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris.forEach { uri -> carrusel.add("data:image/png;base64," + imagenToBase64(context, uri)) }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Editar Evento") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            Modifier.padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //  Portada
            Card(
                Modifier.fillMaxWidth().height(210.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                SafeImage(
                    model = portadaBase64 ?: portadaUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Button(onClick = { pickPortada.launch("image/*") }, Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Image, null)
                Spacer(Modifier.width(6.dp))
                Text("Cambiar Imagen Principal")
            }

            SectionTitle("Información del Evento")

            LabeledInput(nombre, { nombre = it }, "Nombre", Icons.Default.Event)
            LabeledInput(localizacion, { localizacion = it }, "Localización", Icons.Default.Place)
            LabeledInput(descripcion, { descripcion = it }, "Descripción", Icons.Default.Info)
            LabeledInput(precioTxt, { precioTxt = it }, "Precio €", Icons.Default.AttachMoney)
            LabeledInput(aforoTxt, { aforoTxt = it }, "Aforo Máximo", Icons.Default.People)

            SectionTitle("Categoría y Fechas")

            DropdownMenuCategoria(categoria) { categoria = it }


               //  Fecha y hora inicio y fin
                DateTimeInput(
                    text = "Inicio",
                    value = formatearFechayHora(inicioEvento),
                    onClick = {
                        showDateTimePicker(context) { newDateTime ->
                            inicioEvento = newDateTime
                        }
                    },
                )
                DateTimeInput(
                    text = "Fin",
                    value = formatearFechayHora(finEvento),
                    onClick = {
                        showDateTimePicker(context) { newDateTime ->
                            finEvento = newDateTime
                        }
                    }
                )


            SectionTitle("Carrusel")

            var imagenAEliminar by remember { mutableStateOf<String?>(null) }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(carrusel) { img ->
                    Box(Modifier.size(110.dp)) {

                        Card(
                            Modifier.fillMaxSize(),
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
                                contentDescription = "Eliminar Imagen",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }



            if (imagenAEliminar != null) {
                AlertDialog(
                    onDismissRequest = { imagenAEliminar = null },
                    title = { Text("Eliminar imagen") },
                    text = { Text("¿Seguro que quieres eliminar esta imagen del carrusel?") },
                    confirmButton = {
                        Button(onClick = {
                            carrusel.remove(imagenAEliminar)
                            imagenAEliminar = null
                        }) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { imagenAEliminar = null }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            OutlinedButton({ pickCarrusel.launch("image/*") }, Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AddPhotoAlternate, null)
                Spacer(Modifier.width(6.dp))
                Text("Agregar al Carrusel")
            }

            SectionTitle("Invitados")

            InvitadoEditorUIEdit(invitados)

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
                        invitados = invitados.toList() //  Lo que ves es lo que se guarda
                    )

                    eventoViewModel.actualizarEvento(eventoId, dto) {
                        scope.launch { //  Aquí sí usamos coroutine porque showSnackbar es suspend
                            snackbarHostState.showSnackbar(" Evento actualizado")
                            navController.popBackStack()
                        }
                    }
                },
                Modifier.fillMaxWidth()
            ) { Text("Guardar Cambios") }
        }
    }
}

@Composable
fun SafeImage(
    model: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (model.isNullOrBlank()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PhotoCamera, null, tint = Color.Gray)
        }
        return
    }

    val cleanModel = model.replace("data:image/png;base64,", "")
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
                    Icon(Icons.Default.PhotoCamera, null, tint = Color.Gray)
                }
            }
        }
    }
}