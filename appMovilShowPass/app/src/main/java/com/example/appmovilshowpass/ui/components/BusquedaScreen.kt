package com.example.appmovilshowpass.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilshowpass.ui.screens.BusquedaViewModel


@Composable
fun BusquedaScreen(viewModel: BusquedaViewModel = viewModel()) {
    val eventos by viewModel.eventos.collectAsState()

    SearchBarBusqueda(eventos = eventos,viewModel= viewModel)
}

