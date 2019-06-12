package com.example.android.androidskeletonapp.ui.splash;

import android.os.Bundle;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;
import com.example.android.androidskeletonapp.ui.main.MainActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;
import com.facebook.stetho.Stetho;

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

        disposable = Sdk.instantiate(getApplicationContext())
                .andThen(isUserLogged())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isUserLogged -> {
                    if (isUserLogged) {
                        if (hasPrograms()) {
                            ActivityStarter.startActivity(this, ProgramsActivity.class);
                        } else {
                            ActivityStarter.startActivity(this, MainActivity.class);
                        }
                    } else {
                        ActivityStarter.startActivity(this, LoginActivity.class);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    ActivityStarter.startActivity(this, LoginActivity.class);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private Single<Boolean> isUserLogged() {
        return Single.create(emitter -> {
            if (Sdk.isConfigured()) {
                emitter.onSuccess(Sdk.d2().userModule().isLogged().call());
            } else {
                emitter.onSuccess(Boolean.FALSE);
            }
        });
    }

    private boolean hasPrograms() {
        return Sdk.d2().programModule().programs.count() > 0;
    }
}