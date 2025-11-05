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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.example.appmovilshowpass.model.BottomNavItem
import com.example.appmovilshowpass.model.Rol
import com.example.appmovilshowpass.ui.components.AdminFab
import com.example.appmovilshowpass.ui.components.VendedorFab
import com.example.appmovilshowpass.ui.screens.AdminBorrarEventosScreen
import com.example.appmovilshowpass.ui.screens.AdminReportScreen
import com.example.appmovilshowpass.ui.screens.BusquedaScreen
import com.example.appmovilshowpass.ui.screens.CarritoScreen

import com.example.appmovilshowpass.ui.screens.EventoInfo
import com.example.appmovilshowpass.ui.screens.EventoScreen
import com.example.appmovilshowpass.ui.screens.InfoScreen
import com.example.appmovilshowpass.ui.screens.LoginScreen
import com.example.appmovilshowpass.ui.screens.RegisterScreen
import com.example.appmovilshowpass.ui.screens.TicketsScreen
import com.example.appmovilshowpass.ui.screens.UsuarioEditScreen
import com.example.appmovilshowpass.ui.screens.UsuarioScreen
import com.example.appmovilshowpass.ui.screens.VendedorCrearEventoScreen
import com.example.appmovilshowpass.ui.screens.VendedorEditarEventoScreen

import com.example.appmovilshowpass.ui.screens.VendedorMisEventosScreen
import com.example.appmovilshowpass.viewmodel.BusquedaViewModel
import com.example.appmovilshowpass.viewmodel.CarritoViewModel
import com.example.appmovilshowpass.viewmodel.EventoViewModel
import com.example.appmovilshowpass.ui.theme.AppMovilShowpassTheme
import com.example.appmovilshowpass.ui.theme.DarkBlue
import com.example.appmovilshowpass.ui.theme.Roboto
import com.example.appmovilshowpass.viewmodel.TicketViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMovilShowpassTheme {
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

    val context = LocalContext.current

    // Cargar la foto guardada en DataStore al iniciar
    LaunchedEffect(Unit) {
        authViewModel.loadUserPhoto(context)
    }

    val items = listOf(
        //BottomNavItem("Inicio", Icons.Default.Home, "inicio"),
        BottomNavItem("Eventos", Icons.Outlined.Event, "eventos"),
        BottomNavItem("Busqueda", Icons.Outlined.Search, "buscar"),
        BottomNavItem("Carrito", Icons.Outlined.ShoppingCart, "carrito"),
        BottomNavItem("Tickets", Icons.Outlined.QrCode, "tickets"),
        BottomNavItem("Info", Icons.Outlined.Info, "info"),
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
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(
                                fontFamily = Roboto, // Usa la familia de fuentes que definiste
                                fontWeight = FontWeight.ExtraBold, // Usa el peso que quieres (seleccionará roboto_extrabold.ttf)
                                fontSize = 24.sp, // La movemos aquí para que sea parte del estilo
                            ),
                            color = Color.White,
                            modifier = Modifier.clickable()
                            { navController.navigate("eventos") }
                        )
                        Icon(
                            imageVector = Icons.Outlined.LocalActivity,
                            contentDescription = "Icono",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = { navController.navigate("usuario"){
                            // Evitar múltiples copias de la pantalla en la pila
                            launchSingleTop = true
                            // Volver a la pantalla de eventos si ya está en la pila
                            popUpTo("eventos") { saveState = true }
                        } }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Usuario",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
                ),
                modifier = Modifier.height(90.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface, // o primaryContainer
                contentColor = MaterialTheme.colorScheme.onSurface  // color del icono/texto
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        modifier = (Modifier.height(height = 20.dp)),
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 10.sp) },
                        selected = currentRoute == item.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
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
        },
        floatingActionButton = {

            if (authViewModel.currentUser?.rol == Rol.ADMIN) {
                AdminFab(
                    onUsersClick = { navController.navigate("admin_report") },
                    onEventsClick = { navController.navigate("admin_eventos") }
                )
            }

            if (authViewModel.currentUser?.rol == Rol.VENDEDOR) {
                VendedorFab(
                    onCreateClick = { navController.navigate("vendedor_crear") },
                    onEditClick = { navController.navigate("vendedor_editar") }
                )
            }

        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "eventos",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) },
            popEnterTransition = { fadeIn(animationSpec = tween(500)) },
            popExitTransition = { fadeOut(animationSpec = tween(500)) },
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
                EventoInfo(
                    eventoId = eventoId,
                    authViewModel = authViewModel
                )
            }

            composable("buscar") {
                val busquedaViewModel: BusquedaViewModel = viewModel()
                BusquedaScreen(viewModel = busquedaViewModel, navController = navController)
            }

            composable("carrito") {


                val carritoViewModel: CarritoViewModel = viewModel()
                val ticketViewModel: TicketViewModel = viewModel()

                val usuarioId = authViewModel.currentUser?.id ?: 0L

                CarritoScreen(
                    navController = navController,
                    carritoViewModel = carritoViewModel,
                    ticketViewModel = ticketViewModel,
                    usuarioId = usuarioId
                )
            }
            composable("tickets") {
                val ticketViewModel: TicketViewModel = viewModel()

                TicketsScreen(
                    authViewModel = authViewModel,
                    ticketViewModel = ticketViewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("info") {
                InfoScreen(authViewModel, navController)

            }

            composable("usuario") {
                UsuarioScreen(
                    authViewModel = authViewModel,
                    onLoginClick = { navController.navigate("login") },
                    onRegisterClick = { navController.navigate("register") },
                    onEditClick = { navController.navigate("editar_usuario") }
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
            composable("editar_usuario") {
                UsuarioEditScreen(
                    authViewModel = authViewModel,
                    onSaveSuccess = {
                        navController.popBackStack() // volvemos a la pantalla usuario
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
            composable("admin_report") {
                AdminReportScreen(
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("admin_eventos") {
                AdminBorrarEventosScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("vendedor_crear") {
                VendedorCrearEventoScreen(authViewModel, navController)
            }
            composable("vendedor_editar") {
                VendedorMisEventosScreen(authViewModel, navController)

            }

            composable(
                route = "vendedor_editar_evento/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->

                val id = backStackEntry.arguments?.getLong("id") ?: 0L

                VendedorEditarEventoScreen(
                    eventoId = id,
                    authViewModel = authViewModel,
                    navController = navController,
                )
            }

        }

    }
}