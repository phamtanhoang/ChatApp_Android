package com.pth.chatapp_android.ui.signup

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pth.chatapp_android.repository.AuthRepository
import com.pth.chatapp_android.utils.Constant
import com.pth.chatapp_android.utils.Resource
import com.pth.chatapp_android.utils.putAny
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val pref: SharedPreferences
) : ViewModel() {

    private val _signup = MutableLiveData<Resource<Boolean>>()
    val signup: LiveData<Resource<Boolean>> = _signup

    fun signupFunc(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signup.postValue(Resource.Loading())

            val userData = HashMap<String, Any>()
            userData[Constant.KEY_NAME] = name
            userData[Constant.KEY_EMAIL] = email
            userData[Constant.KEY_PASSWORD] = password

            when (val documentReference = authRepository.signup(userData)) {
                is Resource.Success -> {
                    documentReference.data?.let {
                        pref.putAny(Constant.KEY_IS_SIGNED_IN, true)
                        pref.putAny(Constant.KEY_USER_ID, it.id)
                        pref.putAny(Constant.KEY_NAME, name)
                        pref.putAny(Constant.KEY_EMAIL, email)
                        _signup.postValue(Resource.Success(true))
                    }
                }
                is Resource.Error -> {
                    _signup.postValue(documentReference.message?.let { Resource.Error(it) })
                }

                else -> {}
            }

        }
    }
}