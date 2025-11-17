package com.example.appmovilshowpass.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appmovilshowpass.R
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.viewmodel.BusquedaViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarBusqueda(
    eventos: List<Evento>,
    viewModel: BusquedaViewModel,
    navController: NavController,
) {
    // Estados guardados incluso al navegar
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var busquedaRealizada by rememberSaveable { mutableStateOf(false) }
    var bannerVisible by rememberSaveable { mutableStateOf(true) }

    val eventos by viewModel.eventos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        // Barra de búsqueda siempre visible
        DockedSearchBar(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        viewModel.busquedaEventosPorNombre(query)
                        busquedaRealizada = true
                        bannerVisible = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Buscar por nombre...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                    )
                )
            },
            expanded = false,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
            shape = RoundedCornerShape(5.dp),
            colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            content = {}
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Contenido debajo de la barra
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                // Banner la primera vez
                !busquedaRealizada && bannerVisible -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.busqueda),
                            contentDescription = "busqueda",
                            modifier = Modifier.size(275.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Encuentra conciertos, festivales y más",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }

                // Lista de eventos o mensajes
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            when {
                                busquedaRealizada && eventos.isEmpty() -> {
                                    // ⭐ CAMBIO CLAVE: Aplicar .fillParentMaxSize() al ítem
                                    Column(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.no_results),
                                            contentDescription = "No results",
                                            modifier = Modifier.size(300.dp)
                                        )

                                        Spacer(modifier = Modifier.height(18.dp))

                                        Text(
                                            text = "No se encontraron resultados para: \"$query\"",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                busquedaRealizada && eventos.isNotEmpty() -> {
                                    Text(
                                        text = "Mostrando resultados para: \"$query\"",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        items(eventos, key = { it.id }) { evento ->
                            EventoCardHorizontal(
                                evento = evento,
                                modifier = Modifier.clickable {
                                    navController.navigate("evento_info/${evento.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

