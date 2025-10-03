package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.ui.components.BarraCarga
import com.example.appmovilshowpass.ui.components.EventoCard
import com.example.appmovilshowpass.viewmodel.EventoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoScreen(
    viewModel: EventoViewModel = viewModel(),
    navController: NavController
) {
    val eventos by viewModel.eventos.collectAsState() //Lista de eventos desde el ViewModel
    val eventosBoton = listOf("TODOS") + Categoria.values().map { it.name } //Btones de categorias y filtrado
    var isRefreshing by remember { mutableStateOf(false) } //Estado de refresco
    val scope = rememberCoroutineScope() //Corrutina para refresco

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            scope.launch {
                viewModel.obtenerEventosSuspend()
                isRefreshing = false
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    {
        if (eventos.isEmpty()) {
            BarraCarga(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
            // Fila de botones para filtrar por categoria
            {
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
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary,
                                )
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
}
