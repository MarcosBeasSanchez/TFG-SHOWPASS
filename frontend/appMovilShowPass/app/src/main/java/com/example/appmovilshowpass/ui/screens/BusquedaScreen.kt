package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovilshowpass.ui.components.Cabecera
import com.example.appmovilshowpass.ui.components.SearchBarBusqueda
import com.example.appmovilshowpass.viewmodel.BusquedaViewModel


@Composable
fun BusquedaScreen(viewModel: BusquedaViewModel = viewModel(), navController: NavController) {
    val eventos by viewModel.eventos.collectAsState()
    Column(
        modifier = Modifier.padding(vertical = 0.dp, horizontal = 16.dp)
    ) {
        Cabecera("Buscar Eventos", Icons.Default.Search)
        SearchBarBusqueda(eventos = eventos, viewModel = viewModel, navController = navController)
    }
}

