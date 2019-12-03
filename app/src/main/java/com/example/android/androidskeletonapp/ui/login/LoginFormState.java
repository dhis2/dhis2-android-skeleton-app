package com.example.android.androidskeletonapp.ui.login;

import androidx.annotation.Nullable;

class LoginFormState {
    @Nullable
    private Integer serverUrlError;
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer serverUrlError, @Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.serverUrlError = serverUrlError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.serverUrlError = null;
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getServerUrlError() {
        return serverUrlError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
