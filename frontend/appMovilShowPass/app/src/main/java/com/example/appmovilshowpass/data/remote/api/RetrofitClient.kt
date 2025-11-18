package com.example.appmovilshowpass.data.remote.api
import com.example.appmovilshowpass.data.local.BASE_URL //
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // ← LOGS DETALLADOS
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // ← AÑADIDO
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val eventoApiService: EventoApiService by lazy {
        retrofit.create(EventoApiService::class.java)
    }

    val usuarioApiService: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }

    val carritoApiService: CarritoApiService by lazy {
        retrofit.create(CarritoApiService::class.java)
    }

    val ticketApiService: TicketApiService by lazy {
        retrofit.create(TicketApiService::class.java)
    }
}
