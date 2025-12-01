package com.example.appmovilshowpass.data.remote.api
import com.example.appmovilshowpass.data.local.BASE_URL //
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Interceptor utilizado para registrar en el log todas las peticiones y respuestas HTTP.
    // El nivel BODY permite visualizar el contenido completo del mensaje enviado y recibido.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP configurado con el interceptor anterior.
    // OkHttpClient es la base sobre la que Retrofit ejecuta las peticiones.
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Instancia principal de Retrofit. Se genera una única vez gracias a la delegación "lazy".
    // Esta instancia define la URL base del servidor, el cliente HTTP y el convertidor JSON.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio de la API relacionado con la gestión de eventos.
    // Retrofit genera automáticamente la implementación de la interfaz EventoApiService.
    val eventoApiService: EventoApiService by lazy {
        retrofit.create(EventoApiService::class.java)
    }

    // Servicio de la API para operaciones relacionadas con usuarios.
    val usuarioApiService: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }

    // Servicio de la API correspondiente a las funcionalidades del carrito.
    val carritoApiService: CarritoApiService by lazy {
        retrofit.create(CarritoApiService::class.java)
    }

    // Servicio de la API que gestiona la obtención y validación de tickets.
    val ticketApiService: TicketApiService by lazy {
        retrofit.create(TicketApiService::class.java)
    }
}
