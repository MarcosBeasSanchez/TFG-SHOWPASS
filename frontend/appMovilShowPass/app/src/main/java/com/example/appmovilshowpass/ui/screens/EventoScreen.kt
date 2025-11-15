package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
// ... (otras importaciones)
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Importado para el tamaño de la cabecera
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
    val eventos by viewModel.eventos.collectAsState()
    val eventosBoton = listOf("TODOS") + Categoria.values().map { it.name }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 1. ESTADO: Estado para guardar la categoría seleccionada.
    var categoriaSeleccionada by remember { mutableStateOf("TODOS") }

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
                            // Definición de colores para el estado pulsado (opcional, pero mejora la UX)
                            val estaSeleccionado = boton == categoriaSeleccionada
                            val buttonColors = if (estaSeleccionado) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // Relleno
                                    contentColor = MaterialTheme.colorScheme.onPrimary // Texto blanco
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary, // Texto principal
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    // 2. ACTUALIZAR ESTADO: Al hacer click, guardamos la categoría
                                    categoriaSeleccionada = boton
                                    viewModel.filtrarEventosPorCategoria(boton)
                                },
                                modifier = Modifier.padding(3.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = buttonColors // Usamos los colores condicionales
                            ) {
                                Text(boton, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Cabecera con la categoría seleccionada (si no es "TODOS")
                if (categoriaSeleccionada != "TODOS") {
                    item {
                        Text(
                            text = categoriaSeleccionada,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        )
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