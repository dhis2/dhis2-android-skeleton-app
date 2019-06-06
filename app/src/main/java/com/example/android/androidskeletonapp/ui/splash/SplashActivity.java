package com.example.android.androidskeletonapp.ui.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;
import com.example.android.androidskeletonapp.ui.main.MainActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;
import com.facebook.stetho.Stetho;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final static boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        AsyncTask.execute(() -> {
            Sdk.instantiate(getApplicationContext());
            if (isUserLogged()) {
                if (hasPrograms()) {
                    Intent programsActivity = new Intent(getApplicationContext(), ProgramsActivity.class);
                    startActivity(programsActivity);
                } else {
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                }
            } else {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
            finish();
        });
    }

    private boolean isUserLogged() {
        if (Sdk.isConfigured()) {
            try {
                return Sdk.d2().userModule().isLogged().call();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean hasPrograms() {
        return Sdk.d2().programModule().programs.count() > 0;
    }
}