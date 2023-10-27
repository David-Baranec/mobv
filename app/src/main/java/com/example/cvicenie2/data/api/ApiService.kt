package com.example.cvicenie2.data.api
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.cvicenie2.config.AppConfig
import com.example.cvicenie2.data.api.helper.AuthInterceptor
import com.example.cvicenie2.data.api.helper.TokenAuthenticator
import com.example.cvicenie2.data.api.model.GeofenceListResponse
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
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.OkHttpClient
import retrofit2.Call

interface ApiService {
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<RegistrationResponse>

    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<LoginResponse>

    @GET("user/get.php")
    suspend fun getUser(
        @Query("id") id: String
    ): Response<UserResponse>

    @POST("user/refresh.php")
    suspend fun refreshToken(
        @Body refreshInfo: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
    @GET("geofence/list.php")
    suspend fun listGeofence(): Response<List<GeofenceListResponse>>
    @POST("user/refresh.php")
    fun refreshTokenBlocking(
        @Body refreshInfo: RefreshTokenRequest
    ): Call<RefreshTokenResponse>
    companion object {
        fun create(context: Context): ApiService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://zadanie.mpage.sk/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}