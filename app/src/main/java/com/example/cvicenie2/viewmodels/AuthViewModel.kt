package com.example.cvicenie2.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvicenie2.data.DataRepository
import com.example.cvicenie2.data.api.model.PhotoResponse
import com.example.cvicenie2.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult
    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult
    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult
    val imageFile = MutableLiveData<File>()
    val passwordResult: MutableLiveData<String?> get() = _passwordResult
    private val _passwordResult = MutableLiveData<String?>()
    val resetStatus: MutableLiveData<String?> get() = _resetStatus
    private val _resetStatus = MutableLiveData<String?>()
    val resetMessage: MutableLiveData<String?> get() = _resetMessage
    private val _resetMessage = MutableLiveData<String?>()
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeat_password = MutableLiveData<String>()
    val oldPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    private val _photoResult = MutableLiveData<Pair<String, PhotoResponse?>>()
    val photoResult: LiveData<Pair<String, PhotoResponse?>>
        get() = _photoResult

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
            val result =
                dataRepository.changePassword(oldPassword.value ?: "", newPassword.value ?: "");
            _passwordResult.postValue(result)
        }

    }

    fun resetPassword() {
        viewModelScope.launch {
            val result = dataRepository.resetPassword(email.value ?: "");
            _resetStatus.postValue(result.first)
            _resetMessage.postValue(result.second ?: "")

        }
    }
    fun uploadPhoto(imageFileLiveData: MutableLiveData<File?>) {
        imageFileLiveData.observe(viewModelScope.coroutineContext as LifecycleOwner) { imageFile ->
            // imageFile can be null if the LiveData is not set yet or cleared
            if (imageFile != null) {
                viewModelScope.launch {
                    val result = dataRepository.uploadImage(imageFile)
                    _photoResult.postValue(result)
                }
            }
            // If imageFile is null, you can handle it as needed
        }
    }
}