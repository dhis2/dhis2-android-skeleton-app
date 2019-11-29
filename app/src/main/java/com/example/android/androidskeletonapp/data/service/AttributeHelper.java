package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

public class AttributeHelper {

    public static String attributePatientNameUid() {
        return getAttributeUid("GR00 CI - First Name");
    }

    public static String attributePatientIdUid() {
        return getAttributeUid("GR00 CI - Person ID");
    }

    public static String attributeYearOfBirthUid() {
        return getAttributeUid("GR00 CI - Date of birth");
    }

    public static String attributeResidentInCatchmentAreaUid() {
        return getAttributeUid("GR00 CI - Address");
    }


    private static String getAttributeUid(String attributeDisplayName) {
        return Sdk.d2().trackedEntityModule().trackedEntityAttributes()
                .byName().eq(attributeDisplayName)
                .one()
                .blockingGet()
                .uid();
    }
}
