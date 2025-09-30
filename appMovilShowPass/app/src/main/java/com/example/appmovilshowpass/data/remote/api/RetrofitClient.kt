package com.example.appmovilshowpass.data.remote.api
import com.example.appmovilshowpass.data.local.BASE_URL //
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // tu URL base
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val eventoApiService: EventoApiService by lazy {
        retrofit.create(EventoApiService::class.java)
    }

    val usuarioApiService: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }
}