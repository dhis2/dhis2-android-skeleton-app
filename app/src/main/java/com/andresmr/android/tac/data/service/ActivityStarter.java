package com.andresmr.android.tac.data.service;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStarter {

    public static void startActivity(AppCompatActivity activity, Intent intent, boolean finishCurrent) {
        activity.startActivity(intent);
        if (finishCurrent)
            activity.finish();
    }
}
