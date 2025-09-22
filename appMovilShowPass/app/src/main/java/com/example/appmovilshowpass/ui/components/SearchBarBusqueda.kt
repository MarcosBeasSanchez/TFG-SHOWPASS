package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.ui.screens.BusquedaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarBusqueda(eventos: List<Evento>, viewModel: BusquedaViewModel) {
    var query by remember { mutableStateOf("") } // Estado para la consulta de búsqueda
    var expanded by remember { mutableStateOf(true) } // forzamos que la barra esté “expandida”
    val eventos by viewModel.eventos.collectAsState() // Observa los eventos filtrados

    DockedSearchBar(
        inputField = {
            InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { viewModel.busquedaEventosPorNombre(query) },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Buscar por nombre...") }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        shape = RoundedCornerShape(10.dp),
        content =  {
            if (eventos.isEmpty() && query.isNotEmpty()) {
                Text(
                    text = "pulsa la lupa para buscar",
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(20.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .padding(5.dp)

            )
            {
                items(eventos, key = { it.nombre }) { evento ->
                    EventoCardHorizontal(evento)
                }
            }

        }

    )

}