package com.example.appmovilshowpass

import AuthViewModel
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.example.appmovilshowpass.model.BottomNavItem
import com.example.appmovilshowpass.ui.components.BusquedaScreen
import com.example.appmovilshowpass.ui.components.EventoInfo
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
                val primaryColor = MaterialTheme.colorScheme.background.toArgb()
                val darkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f

                // Bloquear orientación vertical
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
        BottomNavItem("Eventos", Icons.Filled.Event, "eventos"),
        BottomNavItem("Busqueda", Icons.Default.Search, "buscar"),
        BottomNavItem("Categorias", Icons.Default.ShoppingCart, "carrito"),
        BottomNavItem("Usuario", Icons.Default.Person, "usuario"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SHOWPASS", 
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Normal,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Outlined.LocalActivity,
                            contentDescription = "Icono",
                            tint = Color.White
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
            navController = navController,
            startDestination = "eventos",
            enterTransition = { fadeIn(animationSpec = tween(1000)) },
            exitTransition = { fadeOut(animationSpec = tween(1000)) },
            popEnterTransition = { fadeIn(animationSpec = tween(1000)) },
            popExitTransition = { fadeOut(animationSpec = tween(1000)) },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("eventos") {
                val eventoViewModel: EventoViewModel = viewModel()
                EventoScreen(viewModel = eventoViewModel, navController = navController)
            }
            composable(
                "evento_info/{eventoId}",
                arguments = listOf(navArgument("eventoId") { type = NavType.LongType })
            ) { backStackEntry ->
                val eventoId = backStackEntry.arguments?.getLong("eventoId") ?: 0L
                EventoInfo(eventoId = eventoId)
            }

            composable("buscar") {
                val busquedaViewModel: BusquedaViewModel = viewModel()
                BusquedaScreen(viewModel = busquedaViewModel, navController = navController)
            }

            composable("carrito") {
                SimpleScreen("Pantalla Carrito")
            }


            composable("usuario") {
                UsuarioScreen(
                    authViewModel = authViewModel,
                    onLoginClick = { navController.navigate("login") },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.popBackStack()
                    },
                    onGoToRegister = { navController.navigate("register") }
                )
            }

            composable("register") {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.popBackStack()
                    },
                    onGoToLogin = { navController.navigate("login") }
                )
            }
        }

    }
}





