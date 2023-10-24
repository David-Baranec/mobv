package com.example.cvicenie2.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.cvicenie2.config.AppConfig
import com.example.cvicenie2.data.api.model.RegistrationResponse
import com.example.cvicenie2.data.api.model.UserRegistrationRequest
import com.example.cvicenie2.data.api.model.UserLoginRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<RegistrationResponse>

    @Headers("x-apikey: ${AppConfig.API_KEY}")
    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<RegistrationResponse>

    companion object {
        fun create(): ApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://zadanie.mpage.sk/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}