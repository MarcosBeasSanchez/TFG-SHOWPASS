package com.example.appmovilshowpass.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovilshowpass.ui.components.SearchBarBusqueda
import com.example.appmovilshowpass.viewmodel.BusquedaViewModel


@Composable
fun BusquedaScreen(viewModel: BusquedaViewModel = viewModel(), navController: NavController) {
    val eventos by viewModel.eventos.collectAsState()
    SearchBarBusqueda(eventos = eventos, viewModel = viewModel, navController = navController)
}

