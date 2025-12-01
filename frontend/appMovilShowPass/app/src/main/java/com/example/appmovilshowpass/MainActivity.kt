package com.example.appmovilshowpass

import AuthViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import com.example.appmovilshowpass.ui.screens.ResultadoQRScreen
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




/**
 * Actividad principal de la aplicación. Es el punto de entrada cuando la app es
 * lanzada normalmente o mediante un deep link.
 *
 * Funciones principales:
 * 1. Procesar intents entrantes (deep links) mediante handleIntent().
 * 2. Inicializar el entorno Compose y renderizar la interfaz principal.
 * 3. Gestionar intents recibidos mientras la actividad está en ejecución
 *    (override onNewIntent).
 *
 * Deep Links:
 * - La aplicación puede ser abierta desde enlaces con un parámetro "contenidoQR".
 * - Cuando se detecta, MainActivity invoca al TicketViewModel para validar el código QR.
 *   Ejemplo de enlace: showpass://validarQR?contenidoQR=XYZ123
 *
 * Estructura general:
 * - Al iniciar, se procesa el intent.
 * - Se carga el tema y se llama a MainScreen(), que contiene toda la navegación.
 * - También se coloca ResultadoQRScreen() en el root para mostrar el estado
 *   cuando se valida un QR desde deep link.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Procesa deep links al iniciar la app
        handleIntent(intent)

        setContent {
            AppMovilShowpassTheme {

                // Contenedor principal de la UI y navegación
                MainScreen()

                // Se dibuja en el nivel raíz para mostrar resultados de validación QR
                ResultadoQRScreen()
            }
        }
    }

    /**
     * Se llama cuando la actividad ya estaba en ejecución y recibe un nuevo Intent
     * (por ejemplo, desde un deep link con la app abierta).
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /**
     * Procesa un Intent recibido que puede contener un deep link.
     *
     * Lógica:
     * - Extrae el parámetro "contenidoQR".
     * - Si existe, se instancia TicketViewModel y se valida el QR.
     *
     * Nota:
     * - Se utiliza viewModels() dentro de la Activity porque no estamos en un @Composable.
     * - Esto garantiza que se obtiene un ViewModel ligado al ciclo de vida de la Activity.
     */
    private fun handleIntent(intent: Intent) {
        val data = intent.data

        data?.let {
            val contenidoQR = it.getQueryParameter("contenidoQR")

            if (contenidoQR != null) {
                Log.d("DeepLink", "Contenido QR recibido: $contenidoQR")

                val ticketViewModel: TicketViewModel by viewModels()

                // Solicita al ViewModel que valide el QR en el backend
                ticketViewModel.validarQr(contenidoQR)
            }
        }
    }
}

/**
 * Pantalla raíz de la aplicación. Contiene:
 *
 * - Control de sesión y autologin.
 * - Barra superior (TopAppBar).
 * - Barra de navegación inferior (Bottom Navigation).
 * - Floating Action Buttons para ADMIN y VENDEDOR.
 * - Sistema de navegación completo mediante NavHost.
 *
 * Comportamiento general:
 * 1. Recupera automáticamente la sesión almacenada (tokens/cookies) mediante autoLogin().
 * 2. Muestra una pantalla de carga hasta que authViewModel confirme el estado de sesión.
 * 3. Una vez lista la sesión, se construye toda la interfaz estructurada:
 *    - TopBar con branding y acceso al perfil.
 *    - BottomBar con navegación entre Eventos, Búsqueda, Carrito, Tickets y Info.
 *    - FAB condicional según rol del usuario.
 *    - NavHost para la navegación interna entre pantallas.
 *
 * Gestión del ViewModel:
 * - AuthViewModel se crea una única vez, accesible desde todo el árbol de navegación.
 * - Otros ViewModels (EventoViewModel, CarritoViewModel, TicketViewModel...) se
 *   inicializan por pantalla mediante viewModel().
 *
 * Roles y permisos:
 * - Si el usuario es ADMIN → aparece AdminFab.
 * - Si el usuario es VENDEDOR → aparece VendedorFab.
 *
 * Navegación:
 * - Cada ruta se registra mediante composable().
 * - Se usan animaciones de transición entre pantallas (fade in/out).
 * - Los popUpTo evitan duplicados en la pila de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    // ViewModel de autenticación único para toda la pantalla
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    // Auto login cuando se abre la app
    LaunchedEffect(Unit) {
        authViewModel.autoLogin(context)
    }

    val isSessionChecked = authViewModel.isSessionChecked

    /**
     * Mientras la sesión NO haya sido validada, se muestra un splash
     * de carga para evitar que la interfaz se renderice en estado inconsistente.
     */
        if (!isSessionChecked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101010)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Cargando sesión y datos iniciales...",
                        color = Color.White
                    )
                }
            }
            return  //  no seguimos dibujando el resto de MainScreen
        }

        LaunchedEffect(Unit) {
            authViewModel.autoLogin(context)
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
                            onClick = {
                                navController.navigate("usuario") {
                                    // Evitar múltiples copias de la pantalla en la pila
                                    launchSingleTop = true
                                    // Volver a la pantalla de eventos si ya está en la pila
                                    popUpTo("eventos") { saveState = true }
                                }
                            }
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
                                        inclusive = true
                                    }
                                    launchSingleTop = true
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
                        authViewModel = authViewModel,
                        carritoViewModel = viewModel(),
                        navController = navController
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