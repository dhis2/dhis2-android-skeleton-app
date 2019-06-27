package com.example.android.androidskeletonapp.data.service.upload;

import com.example.android.androidskeletonapp.data.Sdk;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;

import java.util.List;

public class DataCreator {

    private static String addTEIProjection() throws D2Error {
        String orgUnitUid = Sdk.d2().organisationUnitModule().organisationUnits.one().get().uid();
        String tackedEntityTypeUid = Sdk.d2().trackedEntityModule().trackedEntityTypes.one().get().uid();
        TrackedEntityInstanceCreateProjection projection
                = TrackedEntityInstanceCreateProjection.create(orgUnitUid, tackedEntityTypeUid);
        return Sdk.d2().trackedEntityModule().trackedEntityInstances.add(projection);
    }

    public static void createTEI() {
        try {
            String teiUid = addTEIProjection();
            TrackedEntityModule module = Sdk.d2().trackedEntityModule();
            List<TrackedEntityAttribute> attributes = module.trackedEntityAttributes.get();
            if (attributes.size() > 0) {
                String att0 = attributes.get(0).uid();
                module.trackedEntityAttributeValues.value(att0, teiUid).set("Android");
            }
            if (attributes.size() > 1) {
                String att1 = attributes.get(1).uid();
                module.trackedEntityAttributeValues.value(att1, teiUid).set("" + Math.round(100 * Math.random()));
            }
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
    }
}
