package com.example.android.androidskeletonapp.data.service;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.List;

public class AttributeHelper {

    public static String teiTitle(TrackedEntityInstance trackedEntityInstance) {
        return getAttributeUid(trackedEntityInstance.trackedEntityAttributeValues(), 0);
    }

    public static String teiSubtitle1(TrackedEntityInstance trackedEntityInstance) {
        return getAttributeUid(trackedEntityInstance.trackedEntityAttributeValues(), 1);
    }

    public static String teiSubtitle2First(TrackedEntityInstance trackedEntityInstance) {
        return getAttributeUid(trackedEntityInstance.trackedEntityAttributeValues(), 2);
    }

    public static String teiSubtitle2Second(TrackedEntityInstance trackedEntityInstance) {
        return getAttributeUid(trackedEntityInstance.trackedEntityAttributeValues(), 3);

    }

    public static String teiImage(TrackedEntityInstance trackedEntityInstance) {
        // Return the uid of the TrackedEntityAttribute of the image
        return null;
    }

    private static String getAttributeUid(List<TrackedEntityAttributeValue> attributeValues, int index) {
        if (!attributeValues.isEmpty() && attributeValues.size() >= index + 1) {
            return attributeValues.get(index).trackedEntityAttribute();
        } else {
            return  null;
        }
    }
}
