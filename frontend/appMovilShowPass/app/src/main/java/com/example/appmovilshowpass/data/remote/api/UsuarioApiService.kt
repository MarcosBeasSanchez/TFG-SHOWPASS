package com.example.appmovilshowpass.data.remote.api

import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.appmovilshowpass.data.local.BASE_URL //
import com.example.appmovilshowpass.data.remote.dto.DTOUsuarioReportado
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioLoginBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioModificarSubida
import com.example.appmovilshowpass.model.Login
import com.example.appmovilshowpass.model.Register
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApiService {

    @POST("/tfg/usuario/login")
    suspend fun login(@Body request: Login): DTOusuarioLoginBajada

    @POST("/tfg/usuario/register")
    suspend fun register(@Body request: Register): DTOusuarioBajada

    @POST("/tfg/usuario/update/{id}")
    suspend fun updateUser(@Body usuario: DTOusuarioModificarSubida, @Path("id") id: Long): DTOusuarioBajada

    @PUT("tfg/usuario/reportar")
    suspend fun reportUser(@Query("email") email: String): DTOUsuarioReportado

    @GET("tfg/usuario/findById/{id}")
    suspend fun getUserById(@Path("id") id: Long): DTOusuarioBajada

    @GET("tfg/usuario/findByEmail")
    suspend fun findUserByEmail(@Query("email") email: String): DTOUsuarioReportado

    @GET("tfg/usuario/findAllReportados")
    suspend fun findAllReportados(): List<DTOUsuarioReportado>

    @PUT("tfg/usuario/quitarReport")
    suspend fun removeReport(@Query("email") email: String): DTOUsuarioReportado

    @DELETE("tfg/usuario/delete/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Int



}
