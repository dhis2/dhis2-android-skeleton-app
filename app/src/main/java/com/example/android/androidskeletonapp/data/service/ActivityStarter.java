package com.example.android.androidskeletonapp.data.service;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStarter {

    @Deprecated
    public static void startActivity(AppCompatActivity activity, Class<?> activityClass, boolean finishCurrent) {
        Intent intent = new Intent(activity.getApplicationContext(), activityClass);
        activity.startActivity(intent);
        if (finishCurrent)
            activity.finish();
    }

    public static void startActivity(AppCompatActivity activity, Intent intent, boolean finishCurrent) {
        activity.startActivity(intent);
        if (finishCurrent)
            activity.finish();
    }

    public static void startActivityForResult(AppCompatActivity activity, Intent intent, int requestCode){
        activity.startActivityForResult(intent,requestCode);
    }
}
