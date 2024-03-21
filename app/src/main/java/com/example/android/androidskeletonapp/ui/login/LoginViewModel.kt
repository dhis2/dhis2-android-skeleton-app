package com.example.android.androidskeletonapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.androidskeletonapp.data.Sdk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.hisp.dhis.android.core.maintenance.D2Error

class LoginViewModel internal constructor() : ViewModel() {
    internal val loginResult = MutableLiveData<LoginResult>()
    internal val loginUiState: MutableStateFlow<LoginUiState> = MutableStateFlow(
        LoginUiState(),
    )
    fun login(loginUiState: LoginUiState) {

           Sdk.d2().userModule().logIn(loginUiState.userName!!, loginUiState.password!!, loginUiState.server!!).subscribe(
                { user ->
                    if (user != null) {
                        loginResult.postValue(LoginResult(user))
                    } else {
                        loginResult.postValue(LoginResult("Login error: no user"))
                    }
                },{
                        throwable: Throwable ->
                    var errorCode = ""
                    val d2Error = throwable as D2Error
                    errorCode = ": " + d2Error.errorCode()

                    loginResult.postValue(LoginResult("Login error$errorCode"))
                    throwable.printStackTrace()
                }
            )


    }

    fun setServer(serverUrl: String?) {
        loginUiState.update {
                currentUIState ->
            currentUIState.copy(server = serverUrl)
        }
    }
    fun setUserName(username: String?) {
        loginUiState.update {
                loginUiState ->
            loginUiState.copy(userName = username)
        }
    }

    fun setPassword(username: String?) {
        loginUiState.update {
                loginUiState ->
            loginUiState.copy(userName = username)
        }
    }

    fun initLoginDefaultValues(serverUrl: String?, username: String?, password: String?) {
        loginUiState.update { it.copy(server = serverUrl, userName = username, password = password)  }
    }

}
