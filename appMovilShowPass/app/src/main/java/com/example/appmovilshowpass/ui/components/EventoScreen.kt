package com.example.appmovilshowpass.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.ui.screens.EventoViewModel

@Composable
fun EventoScreen(viewModel: EventoViewModel = viewModel(),navController: NavController) {

    val eventos by viewModel.eventos.collectAsState()

    if (eventos.isEmpty()) {
        BarraCarga()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)

        ) {
            items(eventos, key = { it.id }) { evento ->
                EventoCard(evento, navController = navController)
            }
        }

    }
}

