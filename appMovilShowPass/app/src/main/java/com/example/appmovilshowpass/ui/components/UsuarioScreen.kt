package com.example.appmovilshowpass.ui.components

import AuthViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun UsuarioScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val user = authViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text("Bienvenido, ${user.nombre}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            Text("Email: ${user.email ?: "—"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))
            Button(onClick = { authViewModel.logout() }, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        } else {
            Text("No has iniciado sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
                Text("Iniciar sesión")
            }
            Spacer(Modifier.height(8.dp))
            FilledTonalButton (onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
                Text("Crear cuenta")
            }
        }
    }
}