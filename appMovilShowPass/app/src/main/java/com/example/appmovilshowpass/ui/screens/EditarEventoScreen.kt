package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFecha
import com.example.appmovilshowpass.viewmodel.EventoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEventoScreen(
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
                    value = formatearFecha(inicioEvento),
                    onClick = {
                        showDateTimePicker(context) { newDateTime ->
                            inicioEvento = newDateTime
                        }
                    },
                )
                DateTimeInput(
                    text = "Fin",
                    value = formatearFecha(finEvento),
                    onClick = {
                        showDateTimePicker(context) { newDateTime ->
                            finEvento = newDateTime
                        }
                    }
                )


            SectionTitle("Carrusel")

            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(carrusel) { img ->
                    Card(Modifier.size(110.dp), shape = RoundedCornerShape(10.dp)) {
                        SafeImage(model = img, modifier = Modifier.fillMaxSize())
                    }
                }
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
                            snackbarHostState.showSnackbar("✅ Evento actualizado")
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
fun InvitadoEditorUIEdit(invitados: MutableList<DTOInvitadoSubida>) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var invitadoEditando by remember { mutableStateOf<DTOInvitadoSubida?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        invitados.forEach { inv ->
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Card(Modifier.size(60.dp), shape = RoundedCornerShape(50)) {
                        SafeImage(model = inv.fotoURL, modifier = Modifier.fillMaxSize())
                    }


                    Column(Modifier.weight(1f)) {
                        Text(inv.nombre ?: "Invitado", fontWeight = FontWeight.Bold)
                        Text(inv.apellidos ?: "", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }

                    Button(onClick = {
                        invitadoEditando = inv
                        mostrarDialogo = true
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Icono"
                        )
                    }

                    OutlinedButton(onClick = { invitados.remove(inv) }
                    , colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Icono",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
            invitadoEditando = DTOInvitadoSubida(null, "", "", "", "")
            mostrarDialogo = true }
            , modifier = Modifier.fillMaxWidth().align(Alignment.End)

        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Icono"
            )
            Text("Agregar Invitado", Modifier.padding(start = 6.dp))

        }

        if (mostrarDialogo) {
            InvitadoDialogEditor(invitadoEditando!!, { mostrarDialogo = false }) { actualizado ->
                if (!invitados.contains(actualizado)) invitados.add(actualizado)
                mostrarDialogo = false
            }
        }
    }
}

@Composable
fun InvitadoDialogEditor(
    invitadoInicial: DTOInvitadoSubida,
    onDismiss: () -> Unit,
    onSave: (DTOInvitadoSubida) -> Unit
) {
    var nombre by remember { mutableStateOf(invitadoInicial.nombre ?: "") }
    var apellidos by remember { mutableStateOf(invitadoInicial.apellidos ?: "") }
    var descripcion by remember { mutableStateOf(invitadoInicial.descripcion ?: "") }
    var foto by remember { mutableStateOf(invitadoInicial.fotoURL ?: "") }
    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { foto = "data:image/png;base64," + imagenToBase64(context, it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Invitado") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(nombre, { nombre = it }, Modifier.fillMaxWidth(), label = { Text("Nombre") })
                OutlinedTextField(apellidos, { apellidos = it }, Modifier.fillMaxWidth(), label = { Text("Apellidos") })
                OutlinedTextField(descripcion, { descripcion = it }, Modifier.fillMaxWidth(), label = { Text("Descripción") })

                OutlinedButton(onClick = { picker.launch("image/*") }, Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Image, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cambiar Foto")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    DTOInvitadoSubida(
                        id = invitadoInicial.id,
                        nombre = nombre,
                        apellidos = apellidos,
                        descripcion = descripcion,
                        fotoURL = foto
                    )
                )
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        },
        shape = RoundedCornerShape(16.dp)
    )
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