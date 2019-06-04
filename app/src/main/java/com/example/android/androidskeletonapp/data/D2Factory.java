package com.example.android.androidskeletonapp.data;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.d2manager.D2Configuration;
import org.hisp.dhis.android.core.d2manager.D2Manager;

import java.util.Collections;

public class D2Factory {

    private static D2 d2;

    public static D2Manager getD2Manager(Context context) {
        return new D2Manager(getD2Configuration(context));
    }

    public static D2 getD2(Context context) throws IllegalArgumentException {
        return getD2(context, null);
    }

    public static D2 getD2(Context context, String serverUrl) throws IllegalArgumentException {
        if (d2 != null) {
            return d2;
        }

        D2Manager d2Manager = getD2Manager(context);
        if (!d2Manager.isD2Configured()) {
            if (serverUrl == null) {
                throw new IllegalArgumentException();
            } else {
                d2Manager.configureD2(canonizeUrl(serverUrl));
            }
        }

        d2 = d2Manager.getD2();
        return d2;
    }

    private static D2Configuration getD2Configuration(Context context) {
        return D2Configuration.builder()
                .databaseName("test.db")
                .appName("skeleton_App")
                .appVersion("0.0.1")
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
                .context(context)
                .build();
    }

    private static String canonizeUrl(String serverUrl) {
        String urlToCanonized = serverUrl.trim();
        urlToCanonized = urlToCanonized.replace(" ", "");
        if (urlToCanonized.endsWith("/")) {
            return urlToCanonized;
        } else {
            return urlToCanonized + "/";
        }
    }
}