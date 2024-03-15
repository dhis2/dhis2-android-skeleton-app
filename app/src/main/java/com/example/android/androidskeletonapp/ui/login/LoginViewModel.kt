package com.example.android.androidskeletonapp.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.androidskeletonapp.R
import com.example.android.androidskeletonapp.data.Sdk
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.User

class LoginViewModel internal constructor() : ViewModel() {
    private val loginFormState = MutableLiveData<LoginFormState>()
    private val loginResult = MutableLiveData<LoginResult>()
   /*
    fun getLoginFormState(): LiveData<LoginFormState> {
        return loginFormState
    }

    fun getLoginResult(): LiveData<LoginResult> {
        return loginResult
    }
*/
    fun login(username: String?, password: String?, serverUrl: String?): Single<User> {
        return Sdk.d2().userModule().logIn(username!!, password!!, serverUrl!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { user: User? ->
                if (user != null) {
                    loginResult.postValue(LoginResult(user))
                } else {
                    loginResult.postValue(LoginResult("Login error: no user"))
                }
            }
            .doOnError { throwable: Throwable ->
                var errorCode = ""
                try {
                    val d2Error = throwable as D2Error
                    errorCode = ": " + d2Error.errorCode()
                } catch (ignored: Exception) {
                }
                loginResult.postValue(LoginResult("Login error$errorCode"))
                throwable.printStackTrace()
            }
    }

    fun loginDataChanged(serverUrl: String?, username: String?, password: String?) {
        if (!isServerUrlValid(serverUrl)) {
            loginFormState.value = LoginFormState(R.string.invalid_server_url, null, null)
        }
        if (!isUserNameValid(username)) {
            loginFormState.setValue(LoginFormState(null, R.string.invalid_username, null))
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(LoginFormState(null, null, R.string.invalid_password))
        } else {
            loginFormState.setValue(LoginFormState(true))
        }
    }

    private fun isServerUrlValid(serverUrl: String?): Boolean {
        return if (serverUrl == null) {
            false
        } else Patterns.WEB_URL.matcher(serverUrl).matches()
    }

    private fun isUserNameValid(username: String?): Boolean {
        return if (username == null) {
            false
        } else !username.trim { it <= ' ' }.isEmpty()
    }

    private fun isPasswordValid(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 5
    }
}
