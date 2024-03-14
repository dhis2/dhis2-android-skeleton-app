package com.example.android.androidskeletonapp.ui.login

import org.hisp.dhis.android.core.user.User

internal class LoginResult {
    var success: User? = null
        private set
    var error: String? = null
        private set

    constructor(error: String?) {
        this.error = error
    }

    constructor(success: User?) {
        this.success = success
    }
}
