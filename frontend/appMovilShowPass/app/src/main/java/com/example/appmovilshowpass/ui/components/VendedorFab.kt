package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Composable que muestra un botón flotante (FAB) destinado a los vendedores.
 * Al pulsar el FAB principal se despliega un menú con dos acciones:
 *
 * - Editar eventos creados por el vendedor
 * - Crear un nuevo evento
 *
 * Este componente sirve como acceso rápido a las acciones principales de gestión
 * de eventos por parte del vendedor, y se sitúa visualmente en la esquina inferior derecha.
 *
 * @param onEditClick Acción ejecutada al seleccionar "Editar eventos".
 * @param onCreateClick Acción ejecutada al seleccionar "Crear eventos".
 */
@Composable
fun VendedorFab(
    onEditClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    // Controla si el menú desplegable está visible.
    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(end = 16.dp, bottom = 80.dp)
        ) {

            /**
             * Menú desplegable que aparece cuando el FAB principal es pulsado.
             * Contiene las opciones disponibles para el vendedor.
             */
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Opción para editar eventos ya publicados por el vendedor.
                DropdownMenuItem(
                    text = { Text("Editar eventos") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        expanded = false
                        onEditClick()
                    }
                )

                // Opción para crear un evento nuevo.
                DropdownMenuItem(
                    text = { Text("Crear eventos") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        expanded = false
                        onCreateClick()
                    }
                )
            }
        }

        /**
         * FAB principal que despliega el menú de acciones del vendedor.
         */
        FloatingActionButton(
            onClick = { expanded = true },
            contentColor = Color.White,
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = FloatingActionButtonDefaults.largeShape
        ) {
            Icon(
                imageVector = Icons.Outlined.FolderSpecial,
                contentDescription = "Panel vendedor"
            )
        }
    }
}