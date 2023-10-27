package com.example.cvicenie2.data.db

import androidx.lifecycle.LiveData
import com.example.cvicenie2.data.db.DbDao
import com.example.cvicenie2.data.db.entities.UserEntity

class LocalCache(private val dao: DbDao) {

    suspend fun logoutUser() {
        deleteUserItems()
    }

    suspend fun insertUserItems(items: List<UserEntity>) {
        dao.insertUserItems(items)
    }

    fun getUserItem(uid: String): LiveData<UserEntity?> {
        return dao.getUserItem(uid)
    }

    fun getUsers(): LiveData<List<UserEntity>?> = dao.getUsers()

    suspend fun deleteUserItems() {
        dao.deleteUserItems()
    }

}