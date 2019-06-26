package com.example.android.androidskeletonapp.ui.splash;

import android.os.Bundle;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;
import com.example.android.androidskeletonapp.ui.main.MainActivity;
import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.d2manager.D2Manager;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private final static boolean DEBUG = true;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        disposable = D2Manager.setUp(Sdk.getD2Configuration(this))
                .andThen(isLogged())
                .doOnSuccess(isLogged -> {
                    if(isLogged) {
                        ActivityStarter.startActivity(this, MainActivity.class,true);
                    } else {
                        ActivityStarter.startActivity(this, LoginActivity.class,true);
                    }
                }).doOnError(throwable -> {
                    throwable.printStackTrace();
                    ActivityStarter.startActivity(this, LoginActivity.class,true);
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

    private Single<Boolean> isLogged() {
        return Single.defer(() -> {
            if (D2Manager.isServerUrlSet()) {
                return D2Manager.instantiateD2().flatMap(d2 -> d2.userModule().isLogged());
            } else {
                return Single.just(Boolean.FALSE);
            }
        });
    }
}