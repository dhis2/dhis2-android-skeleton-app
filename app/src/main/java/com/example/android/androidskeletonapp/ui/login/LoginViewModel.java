package com.example.android.androidskeletonapp.ui.login;

import android.util.Patterns;

import com.example.android.androidskeletonapp.R;

import org.hisp.dhis.android.core.d2manager.D2Manager;
import org.hisp.dhis.android.core.user.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LoginViewModel() {
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public Single<User> login(String username, String password, String serverUrl) {
        return D2Manager.setServerUrl(serverUrl).andThen(D2Manager.instantiateD2()).flatMap(d2 ->
            d2.userModule().logIn(username, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(user -> {
                    if (user != null) {
                        loginResult.postValue(new LoginResult(user));
                    } else {
                        loginResult.postValue(new LoginResult(R.string.login_failed));
                    }
                }).doOnError(throwable -> {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                    throwable.printStackTrace();
                });
    }

    void loginDataChanged(String serverUrl, String username, String password) {
        if (!isServerUrlValid(serverUrl)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_server_url, null, null));
        } if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isServerUrlValid(String serverUrl) {
        if (serverUrl == null) {
            return false;
        }
        return Patterns.WEB_URL.matcher(serverUrl).matches();
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
