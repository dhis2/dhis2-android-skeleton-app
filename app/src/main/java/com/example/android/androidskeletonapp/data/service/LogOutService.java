package com.example.android.androidskeletonapp.data.service;

import android.content.Intent;
import android.os.AsyncTask;

import com.example.android.androidskeletonapp.data.D2Factory;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

public class LogOutService {

    public static void logOut(AppCompatActivity activity) {
        AsyncTask.execute(() -> {
            try {
                D2Factory.getD2(activity.getApplicationContext()).wipeModule().wipeEverything();

                Intent loginIntent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                activity.startActivity(loginIntent);
                activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
