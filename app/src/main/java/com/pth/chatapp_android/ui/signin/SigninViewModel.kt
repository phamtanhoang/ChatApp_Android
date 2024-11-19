package com.pth.chatapp_android.ui.signin

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
class SigninViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val pref: SharedPreferences,
) : ViewModel() {

    private val _signin = MutableLiveData<Resource<Boolean>>()
    val signin: LiveData<Resource<Boolean>> = _signin

    fun signinFunc(email: String, password: String) {
        _signin.postValue(Resource.Loading())
        viewModelScope.launch {
            when (val userSignIn = authRepository.signin(email, password)) {
                is Resource.Success -> {
                    userSignIn.data?.let {
                        if (it.documents.size > 0) {
                            val doc = it.documents[0]
                            pref.putAny(Constant.KEY_IS_SIGNED_IN,true)
                            pref.putAny(Constant.KEY_USER_ID,doc.id)
                            doc.getString(Constant.KEY_NAME)
                                ?.let { it1 -> pref.putAny(Constant.KEY_NAME, it1) }
                            doc.getString(Constant.KEY_IMAGE)
                                ?.let { it1 -> pref.putAny(Constant.KEY_IMAGE, it1) }
                            pref.putAny(Constant.KEY_EMAIL,email)
                            _signin.postValue(Resource.Success(true))
                        }
                    }
                }
                is Resource.Error -> {
                    userSignIn.message?.let { _signin.postValue(Resource.Error(it)) }
                }

                else -> {}
            }
        }
    }

    fun clearSignInData(){
        if (_signin.value != null){
            _signin.postValue(Resource.Empty())
        }
    }
}