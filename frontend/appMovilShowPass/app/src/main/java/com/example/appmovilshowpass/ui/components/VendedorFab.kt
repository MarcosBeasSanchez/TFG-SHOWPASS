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

@Composable
fun VendedorFab(
    onEditClick: () -> Unit,
    onCreateClick: () -> Unit
) {
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
            // Menú desplegable
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
// FAB principal
        FloatingActionButton(
            onClick = { expanded = true },
            contentColor = Color.White,
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = FloatingActionButtonDefaults.largeShape
        ) {
            Icon(
                imageVector = Icons.Outlined.FolderSpecial,
                contentDescription = "Panel Admin"
            )
        }
    }
}