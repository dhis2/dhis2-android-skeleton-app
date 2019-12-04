package com.example.android.androidskeletonapp.data.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.android.androidskeletonapp.data.Sdk;

import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import static com.example.android.androidskeletonapp.data.service.AttributeHelper.teiImage;

public class ImageHelper {

    public static Bitmap getBitmap(TrackedEntityInstance trackedEntityInstance) {
        FileResource fileResource = getFileResource(trackedEntityInstance);
        if (fileResource != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(fileResource.path(), options);
        }
        return null;
    }

    private static FileResource getFileResource(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance.trackedEntityAttributeValues() != null) {
            for (TrackedEntityAttributeValue value : trackedEntityInstance.trackedEntityAttributeValues()) {
                if (value.trackedEntityAttribute() != null &&
                        value.trackedEntityAttribute().equals(teiImage(trackedEntityInstance))) {
                    return Sdk.d2().fileResourceModule().fileResources()
                            .uid(value.value())
                            .blockingGet();
                }
            }
        }
        return null;
    }
}
