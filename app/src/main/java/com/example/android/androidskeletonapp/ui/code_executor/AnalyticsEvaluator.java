package com.example.android.androidskeletonapp.ui.code_executor;

import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.utils.Exercise;

import org.hisp.dhis.android.core.analytics.AnalyticsException;
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse;
import org.hisp.dhis.android.core.arch.helpers.Result;

public class AnalyticsEvaluator {
    // Data for analysis

    // Data Elements
    private String opv0Id = "x3Do5e7g4Qo"; // OPV0 doses given
    private String opv1Id = "pikOziyCXbM"; // OPV1 doses given
    private String opv2Id = "O05mAByOgAv"; // OPV2 doses given
    private String opv3Id = "vI2csg55S9C"; // OPV3 doses given

    // Categories
    private String fixedOutreah = "fMZEcRHuamy";    // Category "Location Fixed/Outreach"
    private String fixed = "qkPbeWaFsnU";           // CategoryOption "Fixed"
    private String outreach = "wbrDrL2aYEc";        // CategoryOption "Outreach"

    private String age1year = "YNZyaJHiHYq";        // Category "EPI/nutrition age" (< 1 year, > 1 year)
    private String lower1year = "btOyqprQ9e8";      // CategoryOption "<1y"
    private String greater1year = "GEqzEKCHoGA";    // CategoryOption ">1y"

    // Indicators
    private String opv0Percentage = "UWV8MZEfoC4"; // OPV0 %

    // OrganisationUnit
    private String ngelehunCHC = "DiszpKrYNg8"; // Ngelehun CHC, or you can use relative "UserOrganisationUnit"

    @Exercise(exerciseNumber = "ex13-analytics",
            title = "Analytics engine",
            tips = ""
    )
    Result<DimensionalResponse, AnalyticsException> evaluateAnalytics() {
        return Sdk.d2().analyticsModule().analytics()
                .blockingEvaluate();
    }
}
