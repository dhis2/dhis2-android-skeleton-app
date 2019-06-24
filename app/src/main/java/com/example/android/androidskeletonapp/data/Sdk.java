package com.example.android.androidskeletonapp.data;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.d2manager.D2Configuration;
import org.hisp.dhis.android.core.d2manager.D2Manager;

import java.util.Collections;

public class Sdk {

    public static D2 d2() throws IllegalArgumentException {
        return D2Manager.getD2();
    }

    public static D2Configuration getD2Configuration(Context context) {
        return D2Configuration.builder()
                .appName("skeleton_App")
                .appVersion("0.0.1")
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
                .context(context)
                .build();
    }
}