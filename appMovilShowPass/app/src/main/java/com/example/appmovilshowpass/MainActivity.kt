package com.example.appmovilshowpass

import AuthViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

import com.example.appmovilshowpass.model.BottomNavItem
import com.example.appmovilshowpass.ui.components.BusquedaScreen
import com.example.appmovilshowpass.ui.components.SimpleScreen
import com.example.appmovilshowpass.ui.components.EventoScreen
import com.example.appmovilshowpass.ui.components.LoginScreen
import com.example.appmovilshowpass.ui.components.RegisterScreen
import com.example.appmovilshowpass.ui.components.UsuarioScreen
import com.example.appmovilshowpass.ui.screens.BusquedaViewModel
import com.example.appmovilshowpass.ui.screens.EventoViewModel
import com.example.appmovilshowpass.ui.theme.AppMovilShowpassTheme
import com.example.appmovilshowpass.ui.theme.DarkBlue
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMovilShowpassTheme {
                val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
                val darkIcons = MaterialTheme.colorScheme.primary.luminance() > 0.5f

                // Aplicamos el color dinámico a la status bar
                SideEffect {
                    enableEdgeToEdge(
                        statusBarStyle = if (darkIcons) {
                            SystemBarStyle.light(primaryColor, primaryColor)
                        } else {
                            SystemBarStyle.dark(primaryColor)   // iconos claros
                        },
                        navigationBarStyle = if (darkIcons) {
                            SystemBarStyle.light(primaryColor, primaryColor)
                        } else {
                            SystemBarStyle.dark(primaryColor)
                        }
                    )
                }
                //  pantalla principal
                MainScreen()
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()


    // AuthViewModel global para toda la pantalla (una sola instancia)
    val authViewModel: AuthViewModel = viewModel()

    val items = listOf(
        //BottomNavItem("Inicio", Icons.Default.Home, "inicio"),
        BottomNavItem("Eventos", Icons.Filled.DateRange, "eventos"),
        BottomNavItem("Busqueda", Icons.Default.Search, "buscar"),
        BottomNavItem("Categorias", Icons.Default.List, "categorias"),
        BottomNavItem("Usuario", Icons.Default.Person, "usuario"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "SHOWPASS", 
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    // Si hay usuario logueado, mostramos "Bienvenido <nombre>" a la derecha
                    authViewModel.currentUser?.nombre?.let { nombre ->
                        Text(
                            text = "Bienvenido $nombre",
                            color = Color.White,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
                ),
                modifier = Modifier.height(80.dp)


            )
        },
        bottomBar = {

            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 120.dp) // Ajusta la altura si es necesario, 50dp es un poco pequeño
                    .padding(0.dp),

                ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        modifier = (Modifier.height(height = 20.dp)),
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 10.sp) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController,
            startDestination = "eventos",
            Modifier.padding(innerPadding)
        ) {
            //composable("inicio") { SimpleScreen("Pantalla Inicio") }
            composable("usuario") { SimpleScreen("Pantalla Usuario") }
            composable("eventos") {
                val eventoViewModel: EventoViewModel = viewModel()
                EventoScreen(viewModel = eventoViewModel)
            }
            composable("buscar") {
                val busquedaViewModel: BusquedaViewModel = viewModel()
                BusquedaScreen(viewModel = busquedaViewModel)
            }
            composable("categorias") { SimpleScreen("Pantalla Categorías") }

            composable("usuario") {
                // Estado de sesión: en una app real esto debería venir de un ViewModel o DataStore
                var loggedIn by remember { mutableStateOf(false) }
            }

            composable("usuario") {
                // Pasamos el authViewModel para que UsuarioScreen lea el estado
                UsuarioScreen(
                    authViewModel = authViewModel,
                    onLoginClick = { navController.navigate("login") },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            // Pantalla de login en Compose
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        // Volver a la pantalla anterior (usuario) tras login exitoso
                        navController.popBackStack()
                    },
                    onGoToRegister = { navController.navigate("register") }
                )
            }

            // Pantalla de register en Compose
            composable("register") {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        // Si el register auto-loguea, volver a usuario
                        navController.popBackStack()
                    },
                    onGoToLogin = { navController.navigate("login") }
                )
            }
        }

    }
}





