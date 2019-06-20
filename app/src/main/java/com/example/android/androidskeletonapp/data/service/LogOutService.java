package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LogOutService {

    public static Disposable logOut(AppCompatActivity activity) {
        return Sdk.d2().userModule().logOut()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> ActivityStarter.startActivity(activity, LoginActivity.class),
                                Throwable::printStackTrace);
    }
}
