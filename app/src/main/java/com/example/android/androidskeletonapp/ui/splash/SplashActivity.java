package com.example.android.androidskeletonapp.ui.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.D2Factory;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                D2Factory.getD2(getApplicationContext(), null).userModule().logOut();
                if (isUserLogged()) {
                    Log.v(SplashActivity.class.getSimpleName(), "Launch main the other activity");
                } else {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                }
                finish();
            }
        });
    }

    private boolean isUserLogged() {
        if (D2Factory.getD2Manager(getApplicationContext()).isD2Configured()) {
            try {
                return D2Factory.getD2(getApplicationContext(), null).userModule().isLogged().call();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}