package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.Objects;

public class AttributeHelper {

    private final static String FOCUS_AREA_TET_UID = "Hi5pt67kOuJ";
    private final static String MALARIA_CASE_TET_UID = "rLWqLGKN2kr";

    public static String teiTitle(TrackedEntityInstance trackedEntityInstance) {
        switch (Objects.requireNonNull(trackedEntityInstance.trackedEntityType())) {
            case MALARIA_CASE_TET_UID:
                return getAttributeUid("GR00 CI - First Name");
            case FOCUS_AREA_TET_UID:
                return getAttributeUid("GR00 FI - Focus Name");
            default:
                    return null;
        }
    }

    public static String teiSubtitle1(TrackedEntityInstance trackedEntityInstance) {
        switch (Objects.requireNonNull(trackedEntityInstance.trackedEntityType())) {
            case MALARIA_CASE_TET_UID:
                return getAttributeUid("GR00 CI - Last Name");
            case FOCUS_AREA_TET_UID:
                return getAttributeUid("GR00 FI - Area (kmsq)");
            default:
                    return null;
        }
    }

    public static String teiSubtitle2First(TrackedEntityInstance trackedEntityInstance) {
        switch (Objects.requireNonNull(trackedEntityInstance.trackedEntityType())) {
            case MALARIA_CASE_TET_UID:
                return getAttributeUid("GR00 CI - Date of birth");
            case FOCUS_AREA_TET_UID:
                return getAttributeUid("GR00 FI - Local Focus ID");
            default:
                    return null;
        }
    }

    public static String teiSubtitle2Second(TrackedEntityInstance trackedEntityInstance) {
        switch (Objects.requireNonNull(trackedEntityInstance.trackedEntityType())) {
            case MALARIA_CASE_TET_UID:
                return getAttributeUid("GR00 CI - Sex");
            case FOCUS_AREA_TET_UID:
                return getAttributeUid("GR00 FI - Village Name");
            default:
                    return null;
        }
    }

    public static String teiImage(TrackedEntityInstance trackedEntityInstance) {
        switch (Objects.requireNonNull(trackedEntityInstance.trackedEntityType())) {
            case MALARIA_CASE_TET_UID:
                return getAttributeUid("GR00 CI - Copy of RDT result");
            case FOCUS_AREA_TET_UID:
                return getAttributeUid("GR00 FI - Picture of the foci area");
            default:
                    return null;
        }
    }

    public static String attributeForSearch() {
        return getAttributeUid("GR00 CI - First Name");
    }

    private static String getAttributeUid(String attributeDisplayName) {
        return Sdk.d2().trackedEntityModule().trackedEntityAttributes()
                .byName().eq(attributeDisplayName)
                .one()
                .blockingGet()
                .uid();
    }
}
