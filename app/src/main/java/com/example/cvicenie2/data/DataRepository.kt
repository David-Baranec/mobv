package com.example.cvicenie2.data
import kotlin.math.*
import com.example.cvicenie2.data.model.User
import com.example.cvicenie2.data.api.model.UserRegistrationRequest
import com.example.cvicenie2.data.api.model.UserLoginRequest
import java.io.IOException
import android.content.Context
import android.util.Log
import com.example.cvicenie2.data.api.ApiService
import com.example.cvicenie2.data.api.ApiServiceMcomputing
import com.example.cvicenie2.data.api.model.ChangePasswordRequest
import com.example.cvicenie2.data.api.model.GeofenceUpdateRequest
import com.example.cvicenie2.data.api.model.PhotoResponse
import com.example.cvicenie2.data.api.model.ResetPasswordRequest
import com.example.cvicenie2.data.db.AppRoomDatabase
import com.example.cvicenie2.data.db.LocalCache
import com.example.cvicenie2.data.db.entities.GeofenceEntity
import com.example.cvicenie2.data.db.entities.UserEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class DataRepository private constructor(
    private val service: ApiService,
    private val service2: ApiServiceMcomputing,
    private val cache: LocalCache
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                        ApiService.create(context),
                        ApiServiceMcomputing.create(context),
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
                    ).also { INSTANCE = it }
            }
    }
    suspend fun uploadImage(imageFile: File): Pair<String, PhotoResponse?> {
        try {
            Log.d("DataRepository", "uploadstarted")
            val requestFile = RequestBody.create("image/jpg".toMediaTypeOrNull(), imageFile)
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = service2.uploadImage(imagePart)
            Log.d("DataRepository", response.code().toString())

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair("Image uploaded successfully", it)
                }
            }

            return Pair("Failed to upload image", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to upload image.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to upload image.", null)
    }
    suspend fun apiRegisterUser(
        username: String,
        email: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.registerUser(UserRegistrationRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair(
                        "",
                        User(
                            username,
                            email,
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to create user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to create user.", null)
    }


    suspend fun apiLoginUser(
        username: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.loginUser(UserLoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("Wrong password or username.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            "",
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }
    suspend fun changePassword(
        oldPassword: String,
        newPassword: String): String {
            if (oldPassword.isEmpty()) {
                return "Old password can not be empty"
            }
            if (newPassword.isEmpty()) {
                return "New password can not be empty"
            }
            try{
                val response = service.changePassword(ChangePasswordRequest(oldPassword,newPassword))
                Log.d("DataRepository", response.toString())
                if (response.isSuccessful) {
                    response.body()?.let {
                        return it.status
                    }
                }

                return "Failed to change password"
            }catch (ex: IOException) {
                ex.printStackTrace()
                return "Check internet connection. Failed to change password."
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        return "Fatal error. Failed to change password."

    }
    suspend fun apiGetUser(
        uid: String
    ): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair("", User(it.name, "", it.id, "", "", it.photo))
                }
            }

            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }
    suspend fun resetPassword(email: String):Pair<String, String?> {
        try {
            val response = service.resetPassword(ResetPasswordRequest(email));
            Log.d("DataRepository", response.code().toString())
            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair(it.status, it.message)
                }
            }

            return Pair("Failed to reset password", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to reset password.",null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load users.",null)
    }
    data class LatLng(val latitude: Double, val longitude: Double)
    fun generateRandomLocation(
        center: LatLng,
        radiusInMicrodegrees: Double
    ): LatLng {
        // Radius of the Earth in microdegrees
        val earthRadius = 6371000.0 * 1e6

        // Convert radius from microdegrees to radians
        val radiusRadians = radiusInMicrodegrees / earthRadius

        // Generate a random angle (in radians) for the bearing
        val randomAngle = Math.random() * (2 * PI)

        // Calculate the new latitude using the Haversine formula
        val newLatitude = asin(
            sin(center.latitude.toRadians()) * cos(radiusRadians) +
                    cos(center.latitude.toRadians()) * sin(radiusRadians) * cos(randomAngle)
        )

        // Calculate the new longitude using the Haversine formula
        val newLongitude = center.longitude.toRadians() + atan2(
            sin(randomAngle) * sin(radiusRadians) * cos(center.latitude.toRadians()),
            cos(radiusRadians) - sin(center.latitude.toRadians()) * sin(newLatitude)
        )

        // Convert latitude and longitude from radians to microdegrees and round to 10 decimal places
        val newLatitudeMicrodegrees = Math.round(Math.toDegrees(newLatitude) * 1e6) / 1e6
        val newLongitudeMicrodegrees = Math.round(Math.toDegrees(newLongitude) * 1e6) / 1e6

        return LatLng(newLatitudeMicrodegrees, newLongitudeMicrodegrees)
    }

    // Extension function to convert degrees to radians
    fun Double.toRadians(): Double {
        return this * PI / 180.0
    }
    suspend fun apiListGeofence(): String {
        try {
            val response = service.listGeofence()

            if (response.isSuccessful) {
                response.body()?.list?.let {
                    val users = it.map {
                        val randomLocation = generateRandomLocation(LatLng(response.body()!!.me.lat, response.body()!!.me.lon), response.body()!!.me.radius)

                        UserEntity(
                            it.uid, it.name, it.updated, randomLocation.latitude,randomLocation.longitude, it.radius, it.photo
                        )
                    }
                    cache.insertUserItems(users)
                    return ""
                }
            }

            return "Failed to load users"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to load users."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to load users."
    }

    fun getUsers() = cache.getUsers()
    suspend fun getUsersList() = cache.getUsersList()
    suspend fun insertGeofence(item: GeofenceEntity) {
        cache.insertGeofence(item)
        try {
            val response =
                service.updateGeofence(GeofenceUpdateRequest(item.lat, item.lon, item.radius))

            if (response.isSuccessful) {
                response.body()?.let {

                    item.uploaded = true
                    cache.insertGeofence(item)
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun removeGeofence() {
        try {
            val response = service.deleteGeofence()

            if (response.isSuccessful) {
                response.body()?.let {
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


}