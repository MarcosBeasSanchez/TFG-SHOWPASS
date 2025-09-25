package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.ui.screens.BusquedaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarBusqueda(
    eventos: List<Evento>,
    viewModel: BusquedaViewModel,
    navController: NavController
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var busquedaRealizada by remember { mutableStateOf(false) } // nuevo estado
    val eventos by viewModel.eventos.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(10.dp)
    )
    {
        DockedSearchBar(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        viewModel.busquedaEventosPorNombre(query)
                        busquedaRealizada = true
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Buscar por nombre...") },
                )
            },
            expanded = false,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
            shape = RoundedCornerShape(5.dp),
            content = {}
        )

        // Contenido scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 5.dp)
        ) {
            item {
                if (busquedaRealizada && eventos.isEmpty()) {
                    Text(
                        text = "No se han encontrado eventos con ese nombre",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp)
                    )
                }
            }
            items(eventos, key = { it.id }) { evento ->
                EventoCardHorizontal(evento = evento, modifier = Modifier.clickable {
                    navController.navigate("evento_info/${evento.id}")
                })
            }
        }
    }


}