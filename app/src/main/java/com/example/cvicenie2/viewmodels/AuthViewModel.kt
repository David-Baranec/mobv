package com.example.cvicenie2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvicenie2.data.api.DataRepository
import com.example.cvicenie2.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(username, email, password)
            _registrationResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(username, password)
            _loginResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }
}