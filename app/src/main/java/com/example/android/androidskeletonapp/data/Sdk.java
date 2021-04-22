package com.example.android.androidskeletonapp.data;

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

    public static D2Configuration getD2Configuration(Context context) {
        // This will be null if not debug mode to make sure your data is safe
        Interceptor flipperInterceptor = getFlipperInterceptor(context.getApplicationContext());

        List<Interceptor> networkInterceptors = new ArrayList<>();
        if (flipperInterceptor != null) {
            networkInterceptors.add(flipperInterceptor);
        }

        return D2Configuration.builder()
                .appName("skeleton_App")
                .appVersion("0.0.1")
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .networkInterceptors(networkInterceptors)
                .context(context)
                .build();
    }

    private static Interceptor getFlipperInterceptor(Context context) {
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(context)) {
            NetworkFlipperPlugin networkPluggin = new NetworkFlipperPlugin();
            SoLoader.init(context, false);
            FlipperClient client = AndroidFlipperClient.getInstance(context);
            client.addPlugin(networkPluggin);
            client.addPlugin(new DatabasesFlipperPlugin(context));
            client.addPlugin(new InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()));
            client.start();
            return new FlipperOkhttpInterceptor(networkPluggin);
        } else {
            return null;
        }
    }
}