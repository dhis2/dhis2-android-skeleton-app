package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

public class AttributeHelper {

    public static String attributePatientNameUid() {
        return getAttributeUid("Malaria patient name");
    }

    public static String attributePatientIdUid() {
        return getAttributeUid("Malaria patient id");
    }

    public static String attributeYearOfBirthUid() {
        return getAttributeUid("Malaria patient: Year of birth");
    }

    public static String attributeLegalGuardianUid() {
        return getAttributeUid("Malaria patient: Legal guardian");
    }

    public static String attributeResidentInCatchmentAreaUid() {
        return getAttributeUid("MAL - Resident in catchment area");
    }


    private static String getAttributeUid(String attributeDisplayName) {
        return Sdk.d2().trackedEntityModule().trackedEntityAttributes
                .byName().eq(attributeDisplayName)
                .one()
                .get()
                .uid();
    }
}
