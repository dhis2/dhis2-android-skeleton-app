package com.example.android.androidskeletonapp.ui.login;

import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.user.User;

class LoginResult {
    @Nullable
    private User success;

    @Nullable
    private String error;

    LoginResult(@Nullable String error) {
        this.error = error;
    }

    LoginResult(@Nullable User success) {
        this.success = success;
    }

    @Nullable
    User getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}
