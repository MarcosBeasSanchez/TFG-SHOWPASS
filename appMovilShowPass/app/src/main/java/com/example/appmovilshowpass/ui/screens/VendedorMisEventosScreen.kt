package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.formatearFechaBonita
import com.example.appmovilshowpass.viewmodel.EventoViewModel

@Composable
fun VendedorMisEventosScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    eventoViewModel: EventoViewModel = viewModel()
) {
    val vendedorId = authViewModel.currentUser?.id ?: return
    val eventos by eventoViewModel.eventos.collectAsState()

    //  Detecta si debe recargar eventos al volver de editar
    val refreshTrigger = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refreshEventosVendedor", false)
        ?.collectAsState()

    //  Cuando vuelva y se haya editado algo → refrescamos
    LaunchedEffect(refreshTrigger?.value) {
        if (refreshTrigger?.value == true) {

            eventoViewModel.obtenerEventosDeVendedor(vendedorId)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("refreshEventosVendedor", false)
        }
    }

    //  Carga inicial
    LaunchedEffect(Unit) {
        eventoViewModel.obtenerEventosDeVendedor(vendedorId)
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "Mis Eventos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(eventos) { e ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("vendedor_editar_evento/${e.id}")
                        },
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {

                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(construirUrlImagen(e.imagen))
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build()
                        ),
                        contentDescription = e.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            e.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = formatearFechaBonita(e.inicioEvento),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}