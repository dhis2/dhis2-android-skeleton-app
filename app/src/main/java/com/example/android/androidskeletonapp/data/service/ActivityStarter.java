package com.example.android.androidskeletonapp.data.service;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStarter {

    public static void startActivity(AppCompatActivity activity, Class<?> activityClass) {
        Intent intent = new Intent(activity.getApplicationContext(), activityClass);
        activity.startActivity(intent);
        activity.finish();
    }
}
