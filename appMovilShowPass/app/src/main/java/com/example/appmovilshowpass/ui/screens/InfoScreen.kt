package com.example.appmovilshowpass.ui.screens
import AuthViewModel
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appmovilshowpass.data.local.Desarrollador
import com.example.appmovilshowpass.data.local.DatosDesarrolladores
import java.util.Calendar
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxHeight
import android.graphics.Color as GraphicsColor // Para evitar conflicto de nombres

@Composable
fun InfoScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            item { AboutSection() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { ContactSection() }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {AppFooter(navController)}
        }

}

// --- SECCIÓN SOBRE NOSOTROS (About) ---
@Composable
fun AboutSection() {
    val devs = DatosDesarrolladores.members
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(8.dp)
    ) {
        Text(
            text = "Sobre Nosotros",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Somos dos estudiantes apasionados por la tecnología y el desarrollo de software. Este proyecto es nuestro Trabajo de Fin de Grado (TFG), donde hemos puesto en práctica todos los conocimientos adquiridos durante el Grado Superior de Desarrollo de Aplicaciones Multiplataforma.",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 15.sp
        )
        Text(
            text = "Nuestra misión es facilitar la compra de entradas para eventos, conciertos y espectáculos a través de una plataforma web y una app móvil moderna, intuitiva y segura. Creemos que la tecnología puede mejorar la experiencia de los usuarios y acercarles a sus eventos favoritos de manera sencilla.",
            modifier = Modifier.padding(bottom = 24.dp),
            fontSize = 15.sp
        )

        Text(
            text = "¿Quiénes somos?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        // Tarjetas de Contacto
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            devs.forEach { dev ->
                MemberContactCard(dev, uriHandler)
            }
        }
        Text(
            text = "Juntos hemos trabajado en cada aspecto de este proyecto, desde el diseño del backend y la base de datos hasta la implementación junto con el Frontend y diseños de interfaces, con el objetivo de crear una solución completa y funcional.",
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 15.sp
        )
    }
}

// --- SECCIÓN CONTACTO (Contact) ---
@Composable
fun ContactSection() {
    val URL = """https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d6076.256045189071!2d-3.6002503999999997!3d40.4060146!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd42255d3a247575%3A0xd8a0a40edf810cff!2sIES%20Villablanca!5e0!3m2!1ses!2ses!4v1761835115028!5m2!1ses!2ses" width="100%" height="800" ;"""
    val googleMapsIframeHtml = """
    <iframe src="$URL" 
        style="border:1;" 
        allowfullscreen="True" 
        loading="lazy" 
        referrerpolicy="no-referrer-when-downgrade">
    </iframe>
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Contáctanos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Mapa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            WebViewComposable(
                htmlContent = googleMapsIframeHtml, // Le pasas la cadena HTML con el <iframe>
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun MemberContactCard(dev: Desarrollador, uriHandler: UriHandler) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp),
        // Nota: Se quitó el padding(12.dp) de aquí para dejarlo solo en el Column
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        // 🌟 CAMBIO CLAVE: Column con alineación horizontal centrada
        Column(
            modifier = Modifier
                .width(150.dp)
                .padding(12.dp), // Aplicamos el padding interno aquí
            horizontalAlignment = Alignment.CenterHorizontally // Centra todo el contenido
        ) {
            AsyncImage(
                model = dev.photo,
                contentDescription = dev.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            )
            Text(
                text = dev.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = dev.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = "GitHub",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { uriHandler.openUri(dev.github) }
            )
        }
    }
}

@Composable
fun AppFooter(navController: NavController) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        // 1. CTA Final (Llamada a la Acción)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) ,
                )
                .padding(vertical = 16.dp), // Padding interno para que el texto respire
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Tienes alguna pregunta?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                // 🛠️ Color del texto ajustado para ser visible en el fondo Surface
            )
            Text(
                text = "No dudes en ponerte en contacto con nosotros.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        // 2.
        Column(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = Color.Black)
                    .padding(horizontal = 16.dp), // Padding horizontal para separar de los bordes
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // 🛠️ Usar SpaceBetween para separar
            ) {

                Text(
                    text = "© $currentYear SHOWPASS.",
                    fontSize = 12.sp,
                    color = Color.White
                )

                FooterLink(
                    text = " Repositorio de GitHub SHOWPASS",
                    // 🛠️ Color del texto del link ajustado
                    onClick = { uriHandler.openUri("https://github.com/MarcosBeasSanchez/TFG") }
                )
            }
        }
    }
}

@Composable
fun WebViewComposable(htmlContent: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                // Configuración para permitir JavaScript (necesario para muchos iframes)
                settings.javaScriptEnabled = true
                // Ajusta el contenido al tamaño del WebView
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                setBackgroundColor(GraphicsColor.TRANSPARENT)
                // Asegura que los enlaces se abran dentro del WebView
                webViewClient = WebViewClient()
            }
        },
        update = { webView ->
            // 🌟 CARGA DEL HTML: Aquí es donde se simula el <iframe>
            webView.loadDataWithBaseURL(
                "https://www.google.com", // Es crucial tener una base URL válida
                htmlContent,
                "text/html",
                "utf-8",
                null
            )
        }
    )
}

@Composable
fun FooterLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        modifier = Modifier.clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodySmall,
    )
}