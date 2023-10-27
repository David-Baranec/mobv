package com.example.cvicenie2.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.cvicenie2.config.AppConfig
import com.example.cvicenie2.data.api.model.LoginResponse
import com.example.cvicenie2.data.api.model.RefreshTokenRequest
import com.example.cvicenie2.data.api.model.RefreshTokenResponse
import com.example.cvicenie2.data.api.model.RegistrationResponse
import com.example.cvicenie2.data.api.model.UserRegistrationRequest
import com.example.cvicenie2.data.api.model.UserLoginRequest
import com.example.cvicenie2.data.api.model.UserResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface ApiService {
    @Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<RegistrationResponse>

    @Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<LoginResponse>

    @GET("user/get.php")
    suspend fun getUser(
        @HeaderMap header: Map<String, String>,
        @Query("id") id: String
    ): Response<UserResponse>

    @POST("user/refresh.php")
    suspend fun refreshToken(
        @HeaderMap header: Map<String, String>,
        @Body refreshInfo: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
    companion object {
        fun create(): ApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://zadanie.mpage.sk/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}