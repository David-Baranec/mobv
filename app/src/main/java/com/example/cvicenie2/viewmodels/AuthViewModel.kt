package com.example.cvicenie2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvicenie2.data.DataRepository
import com.example.cvicenie2.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult
    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult
    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult

    val passwordResult : MutableLiveData<String?> get() = _passwordResult
    private val _passwordResult = MutableLiveData<String?>()
    val resetStatus : MutableLiveData<String?> get() = _resetStatus
    private val _resetStatus = MutableLiveData<String?>()
    val resetMessage : MutableLiveData<String?> get() = _resetMessage
    private val _resetMessage = MutableLiveData<String?>()
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeat_password = MutableLiveData<String>()
    val oldPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()

    fun registerUser() {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(
                username.value ?: "",
                email.value ?: "",
                password.value ?: ""
            )
            _registrationResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(username.value ?: "", password.value ?: "")
            _loginResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }
    fun changePassword() {
    viewModelScope.launch {
        val result= dataRepository.changePassword(oldPassword.value ?: "",newPassword.value?:"");
        _passwordResult.postValue(result)
    }

    }
    fun resetPassword(){
        viewModelScope.launch {
            val result= dataRepository.resetPassword(email.value?:"");
            _resetStatus.postValue(result.first)
            _resetMessage.postValue(result.second ?: "")

        }
    }
}