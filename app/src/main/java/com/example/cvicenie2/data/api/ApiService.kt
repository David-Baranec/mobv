package com.example.cvicenie2.data.api
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.cvicenie2.data.api.helper.AuthInterceptor
import com.example.cvicenie2.data.api.helper.TokenAuthenticator
import com.example.cvicenie2.data.api.model.ChangePasswordRequest
import com.example.cvicenie2.data.api.model.ChangePasswordResponse
import com.example.cvicenie2.data.api.model.GeofenceListResponse
import com.example.cvicenie2.data.api.model.GeofenceUpdateRequest
import com.example.cvicenie2.data.api.model.GeofenceUpdateResponse
import com.example.cvicenie2.data.api.model.LoginResponse
import com.example.cvicenie2.data.api.model.PhotoResponse
import com.example.cvicenie2.data.api.model.RefreshTokenRequest
import com.example.cvicenie2.data.api.model.RefreshTokenResponse
import com.example.cvicenie2.data.api.model.RegistrationResponse
import com.example.cvicenie2.data.api.model.ResetPasswordRequest
import com.example.cvicenie2.data.api.model.ResetPasswordResponse
import com.example.cvicenie2.data.api.model.UserRegistrationRequest
import com.example.cvicenie2.data.api.model.UserLoginRequest
import com.example.cvicenie2.data.api.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<RegistrationResponse>

    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<LoginResponse>

    @POST("user/password.php")
    suspend fun changePassword(@Body passwordInfo: ChangePasswordRequest): Response<ChangePasswordResponse>
    @POST("user/reset.php")
    suspend fun resetPassword(@Body passwordInfo: ResetPasswordRequest): Response<ResetPasswordResponse>

    @GET("user/get.php")
    suspend fun getUser(
        @Query("id") id: String
    ): Response<UserResponse>

    @POST("user/refresh.php")
    suspend fun refreshToken(
        @Body refreshInfo: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
    @GET("geofence/list.php")
    suspend fun listGeofence(): Response<GeofenceListResponse>
    @POST("geofence/update.php")
    suspend fun updateGeofence(@Body body: GeofenceUpdateRequest): Response<GeofenceUpdateResponse>

    @DELETE("geofence/update.php")
    suspend fun deleteGeofence(): Response<GeofenceUpdateResponse>
    @POST("user/refresh.php")
    fun refreshTokenBlocking(
        @Body refreshInfo: RefreshTokenRequest
    ): Call<RefreshTokenResponse>
    @Multipart
    @POST("user/photo.php")
    suspend fun uploadImage(
        @Part("image\"; filename=\"photo.jpg\"") file: MultipartBody.Part
    ): Response<PhotoResponse>
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
        fun createForImageUpload(context: Context): ApiService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://upload.mcomputing.eu/")  // Base URL for image upload
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}