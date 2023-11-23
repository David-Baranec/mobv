package com.example.cvicenie2.data

import com.example.cvicenie2.data.model.User
import com.example.cvicenie2.data.api.model.UserRegistrationRequest
import com.example.cvicenie2.data.api.model.UserLoginRequest
import java.io.IOException
import android.content.Context
import android.util.Log
import com.example.cvicenie2.data.api.ApiService
import com.example.cvicenie2.data.api.model.ChangePasswordRequest
import com.example.cvicenie2.data.api.model.GeofenceUpdateRequest
import com.example.cvicenie2.data.api.model.ResetPasswordRequest
import com.example.cvicenie2.data.db.AppRoomDatabase
import com.example.cvicenie2.data.db.LocalCache
import com.example.cvicenie2.data.db.entities.GeofenceEntity
import com.example.cvicenie2.data.db.entities.UserEntity
class DataRepository private constructor(
    private val service: ApiService,
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
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
                    ).also { INSTANCE = it }
            }
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
    suspend fun apiListGeofence(): String {
        try {
            val response = service.listGeofence()

            if (response.isSuccessful) {
                response.body()?.list?.let {
                    val users = it.map {
                        UserEntity(
                            it.uid, it.name, it.updated, response.body()!!.me.lat, response.body()!!.me.lon, it.radius, it.photo
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