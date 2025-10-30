package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import android.content.Context
import android.icu.util.Calendar
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.appmovilshowpass.utils.formatearFecha
import com.example.appmovilshowpass.utils.imagenToBase64
import com.example.appmovilshowpass.viewmodel.EventoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEventoScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    eventoViewModel: EventoViewModel = viewModel()
) {
    val context = LocalContext.current
    val vendedorId = authViewModel.currentUser?.id ?: return
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var localizacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTxt by remember { mutableStateOf("") }
    var aforoTxt by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.MUSICA) }

    var inicioEvento by remember { mutableStateOf("") }
    var finEvento by remember { mutableStateOf("") }

    var portadaBase64 by remember { mutableStateOf<String?>(null) }

    val carrusel = remember { mutableStateListOf<String>() }
    val invitados = remember { mutableStateListOf<DTOInvitadoSubida>() }

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
        Column(
            Modifier.padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ✅ 💎 Portada bonita igual que en EditarEvento
            Card(
                Modifier.fillMaxWidth().height(210.dp),
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
                        Icon(Icons.Default.PhotoCamera, null, tint = Color.Gray)
                    }
                }
            }

            OutlinedButton(
                onClick = { pickPortada.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, null)
                Spacer(Modifier.width(8.dp))
                Text("Seleccionar Imagen Principal")
            }

            // ✅ Campos
            FieldSection("Información del Evento") {
                LabeledInput(nombre, { nombre = it }, "Nombre", Icons.Default.Event)
                LabeledInput(localizacion, { localizacion = it }, "Localización", Icons.Default.Place)
                LabeledInput(descripcion, { descripcion = it }, "Descripción", Icons.Default.Info)
                LabeledInput(precioTxt, { precioTxt = it }, "Precio €", Icons.Default.AttachMoney)
                LabeledInput(aforoTxt, { aforoTxt = it }, "Aforo Máximo", Icons.Default.People)
            }

            SectionTitle("Categoría")
            DropdownMenuCategoria(categoria) { categoria = it }

            FieldSection("Fecha y Hora") {
                DateTimeInput("Inicio", formatearFecha(inicioEvento)) {
                    showDateTimePicker(context) { inicioEvento = it }
                }
                DateTimeInput("Fin", formatearFecha(finEvento)) {
                    showDateTimePicker(context) { finEvento = it }
                }
            }

            // ✅ Carrusel igual con preview como Editar
            FieldSection("Carrusel de imágenes") {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(carrusel) { img ->
                        Card(Modifier.size(110.dp), shape = RoundedCornerShape(10.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    Base64.decode(img, Base64.DEFAULT)
                                ),
                                null,
                                Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                OutlinedButton({ pickCarrusel.launch("image/*") }, Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.AddPhotoAlternate, null)
                    Text("Añadir al Carrusel")
                }
            }

            // ✅ Invitados estilo tarjeta igual EditarEvento
            InvitadoEditorUIEdit(invitados)

            Button(
                onClick = {
                    if (nombre.isBlank() || portadaBase64 == null) {
                        scope.launch { snackbarHostState.showSnackbar("❌ Rellena todo") }
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
                            snackbarHostState.showSnackbar("✅ Evento creado")
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


// ✅ Helpers visuales reutilizables

@Composable
fun FieldSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        content()
    }
}


@Composable
fun InvitadoEditorUI(invitados: MutableList<DTOInvitadoSubida>) {
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenBase64 by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imagenBase64 = imagenToBase64(context, uri) }
    }

    Column {
        Text("Invitados", fontWeight = FontWeight.Bold)

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") })
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })

        Spacer(modifier = Modifier.padding(20.dp))

        Button(onClick = { imagePicker.launch("image/*") }) { Text("Añadir Foto") }

        Spacer(modifier = Modifier.padding(20.dp))

        Button(onClick = {
            if (nombre.isNotBlank() && imagenBase64 != null) {
                invitados.add(DTOInvitadoSubida(id = null,nombre, apellidos, imagenBase64!!, descripcion))
                nombre = ""
                apellidos = ""
                descripcion = ""
                imagenBase64 = null
            }
        }) { Text("Agregar Invitado") }

        if (invitados.isNotEmpty()) {
            Text("✅ ${invitados.size} invitados añadidos")
        }
    }
}

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
                    onDateTimeSelected(fechaHora.toString()) // formato ISO para backend
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

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
@Composable
fun LabeledInput(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(text,
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        textAlign = TextAlign.Start
    )
}

@Composable
fun DateTimeInput(text: String, value: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.CalendarMonth, null)
        Spacer(Modifier.width(6.dp))
        Text(if (value.isEmpty()) text else "$text: $value")
    }
}