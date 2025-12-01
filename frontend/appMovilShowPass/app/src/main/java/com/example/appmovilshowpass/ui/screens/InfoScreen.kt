package com.example.appmovilshowpass.ui.screens
import AuthViewModel
import android.graphics.drawable.Icon
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import com.example.appmovilshowpass.ui.components.Cabecera
import android.graphics.Color as GraphicsColor // Para evitar conflicto de nombres

/**
 * Pantalla de información general de la aplicación.
 *
 * Contiene tres secciones:
 * 1. AboutSection: Información sobre el proyecto y los desarrolladores.
 * 2. ContactSection: Mapa de ubicación del centro educativo.
 * 3. AppFooter: Pie de página con enlaces y año actual.
 *
 * El contenido se organiza mediante LazyColumn para permitir desplazamiento vertical.
 *
 * authViewModel ViewModel del usuario (no utilizado directamente en esta pantalla, pero disponible por coherencia en la navegación).
 * navController Controlador de navegación para permitir futuros accesos desde el footer.
 */
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
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { ContactSection() }
        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { AppFooter(navController) }
    }
}

/**
* Sección "Sobre Nosotros".
*
* Explica el propósito del proyecto, el contexto académico y muestra
* tarjetas individuales con información de cada desarrollador.
*
* Los datos de los desarrolladores se obtienen de la clase DatosDesarrolladores.
* Cada desarrollador se representa visualmente mediante MemberContactCard.
*/
@Composable
fun AboutSection() {
    val devs = DatosDesarrolladores.members
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Cabecera(texto = "Sobre Nosotros", imageVector = Icons.Default.Info)

        Text(
            text = "Somos dos estudiantes apasionados por la tecnología y el desarrollo de software. Este proyecto corresponde a nuestro Trabajo de Fin de Grado, donde aplicamos los conocimientos adquiridos durante el ciclo formativo de Desarrollo de Aplicaciones Multiplataforma.",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 15.sp
        )

        Text(
            text = "Nuestra misión es facilitar la compra de entradas para eventos mediante una plataforma moderna e intuitiva. Este proyecto demuestra la capacidad de integrar backend, frontend y diseño de interfaces en una solución funcional.",
            modifier = Modifier.padding(bottom = 24.dp),
            fontSize = 15.sp
        )

        Text(
            text = "¿Quiénes somos?",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            devs.forEach { dev ->
                MemberContactCard(dev, uriHandler)
            }
        }

        Text(
            text = "Hemos trabajado conjuntamente en todas las fases del desarrollo, integrando diseño, backend, frontend y despliegue para ofrecer una aplicación completa.",
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 15.sp
        )
    }
}

/**
 * Sección de contacto que incluye un mapa embebido de Google Maps
 * mediante un WebView. El mapa carga un iframe con la ubicación del centro
 * educativo asociado al proyecto.
 *
 * Al estar incrustado como HTML, se renderiza mediante WebViewComposable.
 */
@Composable
fun ContactSection() {
    val iframeUrl = """https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d6076.256045189071!2d-3.6002503999999997!3d40.4060146!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd42255d3a247575%3A0xd8a0a40edf810cff!2sIES%20Villablanca!5e0!3m2!1ses!2ses!4v1761835115028!5m2!1ses!2ses"""
    val html = """
        <iframe src="$iframeUrl" style="border:0;" loading="lazy"></iframe>
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Contáctanos",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            WebViewComposable(htmlContent = html, modifier = Modifier.fillMaxSize())
        }
    }
}

/**
 * Tarjeta visual que muestra la información de contacto de un desarrollador.
 *
 * Incluye fotografía, nombre, breve descripción y enlace a GitHub.
 * El enlace se abre en el navegador mediante UriHandler.
 *
 * dev Objeto con los datos del desarrollador.
 * uriHandler Controlador para abrir enlaces externos.
 */
@Composable
fun MemberContactCard(dev: Desarrollador, uriHandler: UriHandler) {
    Card(
        modifier = Modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .width(150.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

/**
 * Pie de página informativo utilizado en la pantalla de información.
 *
 * Incluye:
 * - Un mensaje final invitando al usuario a contactar.
 * - El año actual generado dinámicamente.
 * - Enlace al repositorio de GitHub del proyecto.
 *
 * Este componente permite ampliar la sección inferior de la pantalla
 * con contenido identitario y enlaces relevantes.
 */
@Composable
fun AppFooter(navController: NavController) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Tienes alguna pregunta?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = "No dudes en ponerte en contacto con nosotros.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Black)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "© $currentYear SHOWPASS.",
                fontSize = 12.sp,
                color = Color.White
            )

            FooterLink(
                text = "Repositorio de GitHub SHOWPASS",
                onClick = { uriHandler.openUri("https://github.com/MarcosBeasSanchez/TFG") }
            )
        }
    }
}

/**
 * Encapsula un WebView dentro de un Composable para renderizar contenido HTML.
 *
 * Se utiliza especialmente para cargar iframes (como Google Maps),
 * que normalmente no son compatibles directamente con Compose.
 *
 * Se habilita JavaScript y un cliente WebViewClient para asegurar
 * que los enlaces se abren dentro del WebView.
 *
 * htmlContent Código HTML que se debe renderizar.
 */
@Composable
fun WebViewComposable(htmlContent: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = WebViewClient()
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "https://www.google.com",
                htmlContent,
                "text/html",
                "utf-8",
                null
            )
        }
    )
}

/**
 * Enlace clicable utilizado dentro del pie de página.
 *
 * text Texto visible del enlace.
 * onClick Acción ejecutada al pulsarlo.
 */
@Composable
fun FooterLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        modifier = Modifier.clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodySmall
    )
}