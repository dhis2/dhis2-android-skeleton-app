package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.List;
import java.util.Objects;

public class AttributeHelper {

    // TODO adapt the helper to return the attribute uid you want to see in each field.
    public final static String PERSON_TET_UID = "nEenWmSyUEp";

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
        if (trackedEntityInstance.trackedEntityType() == null) {
            return null;
        }
        if (PERSON_TET_UID.equals(Objects.requireNonNull(trackedEntityInstance.trackedEntityType()))) {
            TrackedEntityAttribute attribute = Sdk.d2().trackedEntityModule().trackedEntityAttributes()
                    .byName().eq("Picture")
                    .one()
                    .blockingGet();
            return attribute != null ? attribute.uid() : null;
        }
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
