package com.example.android.androidskeletonapp.ui.login

internal class LoginFormState {
    var serverUrlError: Int?
        private set
    var usernameError: Int?
        private set
    var passwordError: Int?
        private set
    var isDataValid: Boolean
        private set

    constructor(serverUrlError: Int?, usernameError: Int?, passwordError: Int?) {
        this.serverUrlError = serverUrlError
        this.usernameError = usernameError
        this.passwordError = passwordError
        isDataValid = false
    }

    constructor(isDataValid: Boolean) {
        serverUrlError = null
        usernameError = null
        passwordError = null
        this.isDataValid = isDataValid
    }
}
