package com.example.android.androidskeletonapp.data.service;

import android.content.Context;

import com.example.android.androidskeletonapp.BuildConfig;
import com.facebook.flipper.android.AndroidFlipperClient;
import com.facebook.flipper.android.utils.FlipperUtils;
import com.facebook.flipper.core.FlipperClient;
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin;
import com.facebook.flipper.plugins.inspector.DescriptorMapping;
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin;
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor;
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin;
import com.facebook.soloader.SoLoader;

import okhttp3.Interceptor;

public class FlipperManager {

    public static Interceptor setUp(Context appContext) {
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(appContext)) {
            NetworkFlipperPlugin networkPlugin = new NetworkFlipperPlugin();
            SoLoader.init(appContext, false);
            FlipperClient client = AndroidFlipperClient.getInstance(appContext);
            client.addPlugin(networkPlugin);
            client.addPlugin(new DatabasesFlipperPlugin(appContext));
            client.addPlugin(new InspectorFlipperPlugin(appContext, DescriptorMapping.withDefaults()));
            client.start();
            return new FlipperOkhttpInterceptor(networkPlugin);
        } else {
            return null;
        }
    }
}
