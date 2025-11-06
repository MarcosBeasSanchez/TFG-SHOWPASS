package com.example.appmovilshowpass.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmovilshowpass.data.remote.dto.DTOInvitadoSubida
import com.example.appmovilshowpass.ui.screens.SafeImage
import com.example.appmovilshowpass.utils.imagenToBase64
import kotlin.collections.forEach

@Composable
fun InvitadoEditorUIEdit(invitados: MutableList<DTOInvitadoSubida>) {

    var invitadoEditandoIndex by remember { mutableStateOf(-1) }
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
                        invitadoEditandoIndex = invitados.indexOf(inv) // Guardamos posición
                        invitadoEditando = inv.copy()                  // Copia segura
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
                invitadoEditandoIndex = -1
                invitadoEditando = DTOInvitadoSubida(id = null, nombre = "", apellidos = "", descripcion = "", fotoURL = "")
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
                if (invitadoEditandoIndex >= 0) {
                    // Editar invitado existente en su posición real
                    invitados[invitadoEditandoIndex] = actualizado
                } else {
                    // Agregar nuevo invitado
                    invitados.add(actualizado.copy(id = null))
                }
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
