package com.example.cvicenie2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvicenie2.data.DataRepository
import com.example.cvicenie2.data.db.entities.GeofenceEntity
import com.example.cvicenie2.data.model.User

import kotlinx.coroutines.launch

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _profileResult = MutableLiveData<String>()
    val profileResult: LiveData<String> get() = _profileResult

    private val _userResult = MutableLiveData<User?>()

    val userResult: LiveData<User?> get() = _userResult


    fun loadUser(uid: String) {
        viewModelScope.launch {
            val result = dataRepository.apiGetUser(uid)
            _profileResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun updateGeofence(lat: Double, lon: Double, radius: Double) {
        viewModelScope.launch {
            dataRepository.insertGeofence(GeofenceEntity(lat, lon, radius))
        }
    }
    fun resetUserResult() {
        _userResult.value =null;

    }
    fun removeGeofence() {
        viewModelScope.launch {
            dataRepository.removeGeofence()
        }
    }
}