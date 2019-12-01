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
            exerciseNumber = "ex03",
            version = 1,
            title = "Add the stetho interceptor",
            tips = "D2Configuration accepts network interceptors, use them to add a StethoInterceptor.",
            solutionBranch = "sol03-01"
    )
    public static D2Configuration getD2Configuration(Context context) {
        return D2Configuration.builder()
                .appName("skeleton_App")
                .appVersion("0.0.1")
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .context(context)
                .build();
    }
}