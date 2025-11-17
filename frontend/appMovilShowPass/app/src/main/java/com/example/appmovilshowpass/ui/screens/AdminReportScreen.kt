package com.example.appmovilshowpass.ui.screens

import AuthViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOUsuarioReportado
import com.example.appmovilshowpass.ui.components.Cabecera
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var usuarios by remember { mutableStateOf<List<DTOUsuarioReportado>>(emptyList()) }
    var emailToSearch by remember { mutableStateOf("") }
    var usuarioEncontrado by remember { mutableStateOf<DTOUsuarioReportado?>(null) }
    var mensajeBusqueda by remember { mutableStateOf<String?>(null) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            usuarios = RetrofitClient.eventoApiService.findAllReportados()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Cabecera(texto = "Administrar usuarios", imageVector = Icons.Default.Report)

        //  Buscador
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = emailToSearch,
                onValueChange = { emailToSearch = it },
                label = { Text("Buscar Usuario por Email") },
                modifier = Modifier.weight(1f)

            )

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val user = RetrofitClient.eventoApiService.findUserByEmail(emailToSearch)
                            usuarioEncontrado = user
                            mensajeBusqueda = "Usuario ${user.email} encontrado."
                        } catch (e: Exception) {
                            usuarioEncontrado = null
                            mensajeBusqueda = "No se encontró ningún usuario con ese email."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBDEFB))
            ) {
                Text("Buscar", color = Color.Black)
            }
        }

        mensajeBusqueda?.let {
            Spacer(Modifier.height(8.dp))
            val color = if (usuarioEncontrado != null) Color(0xFF2E7D32) else Color.Red
            Text(text = it, color = color)
        }

        Spacer(Modifier.height(12.dp))

        //  Usuario encontrado
        usuarioEncontrado?.let { usuario ->
            val colorBorde = if (usuario.reportado) Color(0xFFFF7043) else Color.LightGray
            val textoReportado = if (usuario.reportado) "Sí" else "No"

            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, colorBorde),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("ID: ${usuario.id}")
                    Text("Email: ${usuario.email}")
                    Text("Nombre: ${usuario.nombre}")
                    Text("Reportado: $textoReportado")

                    Spacer(Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val reportColor =
                            if (usuario.reportado) Color(0xFFFFCC80) else Color(0xFFCFD8DC)
                        val reportText =
                            if (usuario.reportado) "Des-reportar usuario" else "Reportar usuario"

                        Button(
                            onClick = { showConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = reportColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(reportText, color = Color.Black)
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xD0FD0000)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Eliminar usuario",color = Color.White)
                        }
                    }
                }
            }

            //  Diálogo confirmar reportar/desreportar
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = {
                        Text(
                            if (usuario.reportado) "Des-reportar usuario"
                            else "Reportar usuario",
                            color = Color.Black
                        )
                    },
                    text = {
                        Text(
                            if (usuario.reportado)
                                "¿Estás seguro de quitar el reporte de este usuario?"
                            else
                                "¿Deseas reportar a este usuario?",
                            color = Color.Black
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    if (usuario.reportado)
                                        RetrofitClient.eventoApiService.removeReport(usuario.email)
                                    else
                                        RetrofitClient.eventoApiService.reportUser(usuario.email)

                                    usuarios = RetrofitClient.eventoApiService.findAllReportados()
                                    usuarioEncontrado = usuario.copy(reportado = !usuario.reportado)
                                    showConfirmDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFD8DC))
                        ) {
                            Text("Confirmar", color = Color.Black)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showConfirmDialog = false }) {
                            Text("Cancelar", color = Color.Black)
                        }
                    },
                    containerColor = Color.White
                )
            }

            //  Diálogo confirmar eliminar
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Eliminar usuario", color = Color.Black) },
                    text = {
                        Text(
                            "¿Seguro que deseas eliminar este usuario? Esta acción no se puede deshacer.",
                            color = Color.Black
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    RetrofitClient.usuarioApiService.deleteUser(usuario.id)
                                    usuarios = RetrofitClient.eventoApiService.findAllReportados()
                                    usuarioEncontrado = null
                                    showDeleteDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCDD2))
                        ) {
                            Text("Eliminar", color = Color.Black)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar", color = Color.Black)
                        }
                    },
                    containerColor = Color.White
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        //  Lista de usuarios reportados (vertical)
        Text(
            text = "Usuarios Reportados (${usuarios.size})",
            fontWeight = FontWeight.SemiBold
        )

        Divider( modifier = Modifier.padding(vertical = 8.dp))

        if (usuarios.isEmpty()) {
            Text(
                text = "No hay usuarios reportados.",
                modifier = Modifier.padding(top = 8.dp),
                color = Color.Gray
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(usuarios) { usuario ->
                    Card(
                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(usuario.nombre, fontWeight = FontWeight.Bold)
                            Text(usuario.email)
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .background(Color.Red, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Reportado", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

