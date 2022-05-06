package com.example.android.androidskeletonapp.data;

import android.content.Context;

import com.example.android.androidskeletonapp.data.service.FlipperManager;
import com.example.android.androidskeletonapp.data.utils.Exercise;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Configuration;
import org.hisp.dhis.android.core.D2Manager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

public class Sdk {

    public static D2 d2() throws IllegalArgumentException {
        return D2Manager.getD2();
    }

    public static List<Interceptor> getNetworkInterceptors(Context context) {
        Interceptor flipperInterceptor = FlipperManager.setUp(context.getApplicationContext());

        List<Interceptor> networkInterceptors = new ArrayList<>();
        if (flipperInterceptor != null) {
            networkInterceptors.add(flipperInterceptor);
        }

        return networkInterceptors;
    }

    @Exercise(
            exerciseNumber = "ex01",
            title = "SDK Configuration",
            tips = "Use D2Configuration.builder(). " +
                    "Set the context, " +
                    "set your username as appName, " +
                    "set version 1.0, " +
                    "set timeouts to 2 minutes (connectTimeoutInSeconds, readTimeoutInSeconds and writeTimeoutInSeconds) " +
                    "set the networkInterceptors using the getNetworkInterceptors(context) method."
    )
    public static D2Configuration getD2Configuration(Context context) {
        // TODO
        return null;
    }
}