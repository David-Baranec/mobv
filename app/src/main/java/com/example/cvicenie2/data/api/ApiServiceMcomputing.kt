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

interface ApiServiceMcomputing {
    @Multipart
    @POST("user/photo.php")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<PhotoResponse>
    companion object {
        fun create(context: Context): ApiServiceMcomputing {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://upload.mcomputing.eu/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiServiceMcomputing::class.java)
        }
    }
}