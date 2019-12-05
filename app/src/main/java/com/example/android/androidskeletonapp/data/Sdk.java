package com.example.android.androidskeletonapp.data;

import android.content.Context;

import com.example.android.androidskeletonapp.data.utils.Exercise;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Configuration;
import org.hisp.dhis.android.core.D2Manager;

public class Sdk {

    public static D2 d2() throws IllegalArgumentException {
        return D2Manager.getD2();
    }

    @Exercise(
            exerciseNumber = "ex01a",
            title = "SDK Configuration",
            tips = "Use D2Configuration.builder() Set your username as appName, set version 1.0"
    )
    public static D2Configuration getD2Configuration(Context context) {
        return null;
    }
}