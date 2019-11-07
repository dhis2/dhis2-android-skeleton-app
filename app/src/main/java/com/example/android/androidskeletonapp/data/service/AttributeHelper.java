package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

public class AttributeHelper {

    public static String attributePatientNameUid() {
        return getAttributeUid("First Name");
    }

    public static String attributePatientIdUid() {
        return getAttributeUid("National identifier");
    }

    public static String attributeYearOfBirthUid() {
        return getAttributeUid("Date of birth");
    }

    public static String attributeLegalGuardianUid() {
        return getAttributeUid("Malaria patient: Legal guardian");
    }

    public static String attributeResidentInCatchmentAreaUid() {
        return getAttributeUid("Residence location");
    }


    private static String getAttributeUid(String attributeDisplayName) {
        return Sdk.d2().trackedEntityModule().trackedEntityAttributes()
                .byName().eq(attributeDisplayName)
                .one()
                .blockingGet()
                .uid();
    }
}
