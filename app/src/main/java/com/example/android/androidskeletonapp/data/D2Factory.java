package com.example.android.androidskeletonapp.data;

import android.content.Context;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.d2manager.D2Configuration;
import org.hisp.dhis.android.core.d2manager.D2Manager;

import okhttp3.HttpUrl;

public class D2Factory {

    public static D2 create(Context context, String serverUrl) {

        D2Configuration d2Configuration = D2Configuration.builder()
                .databaseName("test.db")
                .appName("skeleton_App")
                .appVersion("0.0.1")
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .context(context)
                .build();

        D2Manager d2Manager = new D2Manager(d2Configuration);
        d2Manager.configureD2(getConfiguration(serverUrl));

        return d2Manager.getD2();
    }

    private static Configuration getConfiguration(String serverUrl) {
        HttpUrl httpUrl = HttpUrl.parse(canonizeUrl(serverUrl));
        return Configuration.builder()
                .serverUrl(httpUrl)
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