package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
 * Composable que muestra un botón flotante (FAB) destinado a funciones administrativas.
 * Al pulsar el FAB principal se despliega un menú con acciones:
 * - Ver usuarios reportados
 * - Eliminar eventos
 *
 * Este componente se muestra en la esquina inferior derecha de la pantalla.
 *
 * @param onUsersClick Acción ejecutada al seleccionar "Usuarios reportados".
 * @param onEventsClick Acción ejecutada al seleccionar "Eliminar eventos".
 */
@Composable
fun AdminFab(
    onUsersClick: () -> Unit,
    onEventsClick: () -> Unit,
) {
    // Controla si el menú de opciones está abierto o cerrado.
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
             * Contiene las acciones administrativas disponibles.
             */
            DropdownMenu(
                expanded = expanded,
                containerColor = MenuDefaults.containerColor,
                onDismissRequest = { expanded = false }
            ) {
                // Acción para ver usuarios reportados.
                DropdownMenuItem(
                    text = { Text("Usuarios reportados") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        expanded = false
                        onUsersClick()
                    }
                )

                // Acción para eliminar eventos.
                DropdownMenuItem(
                    text = { Text("Eliminar eventos") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        expanded = false
                        onEventsClick()
                    }
                )
            }
        }

        /**
         * FAB principal que abre el menú administrativo.
         * Representa el acceso al panel de control del administrador.
         */
        FloatingActionButton(
            onClick = { expanded = true },
            contentColor = Color.White,
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = FloatingActionButtonDefaults.largeShape
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Panel Admin"
            )
        }
    }
}