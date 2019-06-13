package com.example.android.androidskeletonapp.data;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.d2manager.D2Configuration;
import org.hisp.dhis.android.core.d2manager.D2Manager;

import java.util.Collections;

import io.reactivex.Completable;

public class Sdk {

    private static D2Manager d2Manager;

    public static Completable instantiate(Context context) {
        return Completable.fromCallable(() -> d2Manager = new D2Manager(getD2Configuration(context)));
    }

    public static D2 d2() throws IllegalArgumentException {
        return d2Manager.getD2();
    }

    public static boolean isConfigured() throws IllegalArgumentException {
        return d2Manager.isD2Configured();
    }

    public static void configureServer(String serverUrl) throws IllegalArgumentException {
        if (serverUrl == null) {
            throw new IllegalArgumentException();
        }

        String canonizedUrl = canonizeUrl(serverUrl);
        if (!d2Manager.isD2Configured() || !d2().systemInfoModule().systemInfo.get().contextPath().equals(canonizedUrl)) {
            d2Manager.configureD2(canonizedUrl);
        }
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