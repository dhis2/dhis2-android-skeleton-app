package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

public class SyncStatusHelper {

    public static boolean isMetadataSynced() {
        return Sdk.d2().programModule().programs.count() > 0 && Sdk.d2().dataSetModule().dataSets.count() > 0;
    }

    public static boolean isDataSynced() {
        return Sdk.d2().trackedEntityModule().trackedEntityInstances.count() > 0;
    }
}
