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

/**
 * Composable que permite gestionar (añadir, editar y eliminar) los invitados asociados a un evento.
 * Muestra una lista editable donde cada invitado aparece dentro de una tarjeta con su foto y datos.
 * Al pulsar el botón de editar o agregar, se abre un diálogo para modificar los campos del invitado.
 *
 * La lista recibida como parámetro es mutable y se modifica directamente, actuando como fuente de datos.
 *
 * invitados Lista mutable de invitados que será actualizada según las acciones del usuario.
 */
@Composable
fun InvitadoEditorUIEdit(invitados: MutableList<DTOInvitadoSubida>) {

    // Índice del invitado que se está editando (si es -1 significa "nuevo invitado").
    var invitadoEditandoIndex by remember { mutableStateOf(-1) }

    // Controla la visibilidad del diálogo de edición/creación.
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Objeto temporal que se edita en el diálogo.
    var invitadoEditando by remember { mutableStateOf<DTOInvitadoSubida?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        /**
         * Iteración sobre la lista de invitados para mostrar una tarjeta por cada uno.
         */
        invitados.forEach { inv ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    // Foto del invitado en formato circular.
                    Card(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        SafeImage(model = inv.fotoURL, modifier = Modifier.fillMaxSize())
                    }

                    // Datos del invitado (nombre y apellidos).
                    Column(modifier = Modifier.weight(1f)) {
                        Text(inv.nombre ?: "Invitado", fontWeight = FontWeight.Bold)
                        Text(
                            inv.apellidos ?: "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    /**
                     * Botón para editar al invitado.
                     * Se guarda una copia del invitado y su posición para editarlo después.
                     */
                    Button(onClick = {
                        invitadoEditandoIndex = invitados.indexOf(inv)
                        invitadoEditando = inv.copy()
                        mostrarDialogo = true
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Editar invitado"
                        )
                    }

                    /**
                     * Botón para eliminar el invitado de la lista.
                     */
                    OutlinedButton(
                        onClick = { invitados.remove(inv) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Eliminar invitado",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        /**
         * Botón para agregar un nuevo invitado.
         * Inicializa un objeto vacío y abre el diálogo.
         */
        OutlinedButton(
            onClick = {
                invitadoEditandoIndex = -1
                invitadoEditando = DTOInvitadoSubida(
                    id = null,
                    nombre = "",
                    apellidos = "",
                    descripcion = "",
                    fotoURL = ""
                )
                mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Agregar invitado"
            )
            Text("Agregar Invitado", modifier = Modifier.padding(start = 6.dp))
        }

        /**
         * Componente del diálogo, mostrado solo cuando mostrarDialogo es true.
         * Al guardar, actualiza o agrega el invitado en la lista principal.
         */
        if (mostrarDialogo) {
            InvitadoDialogEditor(
                invitadoInicial = invitadoEditando!!,
                onDismiss = { mostrarDialogo = false }
            ) { actualizado ->

                if (invitadoEditandoIndex >= 0) {
                    // Se modifica un invitado ya existente
                    invitados[invitadoEditandoIndex] = actualizado
                } else {
                    // Se añade un invitado nuevo
                    invitados.add(actualizado.copy(id = null))
                }

                mostrarDialogo = false
            }
        }
    }
}

/**
 * Diálogo modal para editar o crear un invitado de un evento.
 * Permite modificar nombre, apellidos, descripción y foto.
 *
 * Incluye un selector de imágenes basado en ActivityResultContracts.GetContent(),
 * que convierte la imagen seleccionada en Base64 para enviarla al backend.
 *
 * invitadoInicial Datos iniciales del invitado (vacío si es nuevo).
 * onDismiss Acción ejecutada al cerrar el diálogo sin guardar.
 * onSave Acción ejecutada al confirmar los cambios, devolviendo el invitado actualizado.
 */
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

    /**
     * Selector de imágenes que abre la galería del dispositivo.
     * Al seleccionar una imagen, se convierte a Base64 y se asigna al campo foto.
     */
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            foto = "data:image/png;base64," + imagenToBase64(context, it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Invitado") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre") }
                )

                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Apellidos") }
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción") }
                )

                OutlinedButton(
                    onClick = { picker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Foto")
                }
            }
        },

        /**
         * Botón para guardar los cambios.
         * Construye un nuevo DTOInvitadoSubida con los valores actualizados.
         */
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        DTOInvitadoSubida(
                            id = invitadoInicial.id,
                            nombre = nombre,
                            apellidos = apellidos,
                            descripcion = descripcion,
                            fotoURL = foto
                        )
                    )
                }
            ) {
                Text("Guardar")
            }
        },

        // Botón de cancelación
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },

        shape = RoundedCornerShape(16.dp)
    )
}