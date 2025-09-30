package com.example.appmovilshowpass.ui.components

import AuthViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOUsuarioReportado
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosReportScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    // Estado que guarda la lista de usuarios reportados
    var usuarios by remember { mutableStateOf<List<DTOUsuarioReportado>>(emptyList()) }
    // Estado para introducir el email a reportar
    var emailToReport by remember { mutableStateOf("") }

    // Cuando se abre la pantalla, pedimos al backend los usuarios reportados
    LaunchedEffect(Unit) {
        scope.launch {
            usuarios = RetrofitClient.eventoApiService.findAllReportados()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios Reportados") },
                navigationIcon = {
                    // Botón atrás
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 🔹 Campo para escribir un email y reportar manualmente
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = emailToReport,
                    onValueChange = { emailToReport = it },
                    label = { Text("Email a reportar") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            if (emailToReport.isNotBlank()) {
                                // Reportamos usuario
                                RetrofitClient.eventoApiService.reportUser(emailToReport)
                                // Refrescamos la lista de reportados
                                usuarios = RetrofitClient.eventoApiService.findAllReportados()
                                // Limpiamos el campo
                                emailToReport = ""
                            }
                        }
                    }
                ) {
                    Text("Reportar")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // 🔹 Lista de usuarios reportados
            LazyColumn {
                items(usuarios) { usuario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Nombre: ${usuario.nombre}")
                            Text("Email: ${usuario.email}")

                            Spacer(Modifier.height(8.dp))

                            // Botón para quitar reporte
                            Button(
                                onClick = {
                                    scope.launch {
                                        RetrofitClient.eventoApiService.removeReport(usuario.email)
                                        usuarios =
                                            RetrofitClient.eventoApiService.findAllReportados()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Quitar reporte", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
