package com.example.android.androidskeletonapp.data.service;

import android.content.Intent;
import android.os.AsyncTask;

import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

public class LogOutService {

    public static void logOut(AppCompatActivity activity) {
        AsyncTask.execute(() -> {
            try {
                Sdk.d2().userModule().logOut().call();

                Intent loginIntent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                activity.startActivity(loginIntent);
                activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
