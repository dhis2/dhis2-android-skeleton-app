package com.example.android.androidskeletonapp.ui.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;
import com.example.android.androidskeletonapp.ui.main.MainActivity;

import org.hisp.dhis.android.core.D2Manager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        disposable = D2Manager.instantiateD2(Sdk.getD2Configuration(this))
                .flatMap(d2 -> d2.userModule().isLogged())
                .doOnSuccess(isLogged -> {
                    if (isLogged) {
                        ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this),true);
                    } else {
                        ActivityStarter.startActivity(this, LoginActivity.getLoginActivityIntent(this),true);
                    }
                }).doOnError(throwable -> {
                    throwable.printStackTrace();
                    ActivityStarter.startActivity(this, LoginActivity.getLoginActivityIntent(this),true);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}