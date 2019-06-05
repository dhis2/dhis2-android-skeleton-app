package com.example.android.androidskeletonapp.ui.login;

import org.hisp.dhis.android.core.user.User;

import androidx.annotation.Nullable;

class LoginResult {
    @Nullable
    private User success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
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
    Integer getError() {
        return error;
    }
}
