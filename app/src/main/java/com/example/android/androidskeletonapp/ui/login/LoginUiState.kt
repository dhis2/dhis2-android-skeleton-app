package com.example.android.androidskeletonapp.ui.login

import android.util.Patterns

data class LoginUiState(
    val server: String? = "",
    val userName: String? = "",
    val password: String? = "",
    var isLoading: Boolean = false,
) {
    fun isLoginEnabled() = (this.isPasswordValid() && this.isServerUrlValid() && this.isPasswordValid())
    fun isServerUrlValid(): Boolean {
        return if (server == null) {
            false
        } else {
            Patterns.WEB_URL.matcher(server).matches()
        }
    }

    fun isUserNameValid(): Boolean {
        return if (userName == null) {
            false
        } else {
            !userName.trim { it <= ' ' }.isEmpty()
        }
    }

    fun setIsLoading(state: Boolean) {
        isLoading = state
    }

    fun isPasswordValid(): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 5
    }
}
