package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.ui.screens.EventoViewModel
import com.example.appmovilshowpass.ui.theme.Typography

@Composable
fun EventoScreen(
    viewModel: EventoViewModel = viewModel(),
    navController: NavController
) {
    val eventos by viewModel.eventos.collectAsState()
    val eventosBoton = listOf("TODOS") + Categoria.values().map { it.name }


    if (eventos.isEmpty()) {
        BarraCarga()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            item {

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(eventosBoton) { boton ->
                        OutlinedButton(
                            onClick = { viewModel.filtrarEventosPorCategoria(boton) },
                            modifier = Modifier.padding(3.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(boton)
                        }
                    }
                }
            }
            //  Items con las tarjetas de eventos
            items(eventos, key = { it.id }) { evento ->
                EventoCard(evento, navController = navController)
            }
        }
    }
}
