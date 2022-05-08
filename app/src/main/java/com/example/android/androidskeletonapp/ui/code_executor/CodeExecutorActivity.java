package com.example.android.androidskeletonapp.ui.code_executor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.analytics.AnalyticsException;
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse;
import org.hisp.dhis.android.core.arch.helpers.Result;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CodeExecutorActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView executingNotificator;
    private TextView resultNotificator;

    private Disposable disposable;

    public static Intent getIntent(Context context) {
        return new Intent(context, CodeExecutorActivity.class);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_executor);
        Toolbar toolbar = findViewById(R.id.codeExecutorToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        executingNotificator = findViewById(R.id.codeExecutorNotificator);
        resultNotificator = findViewById(R.id.resultNotificator);
        progressBar = findViewById(R.id.codeExecutorProgressBar);
        FloatingActionButton codeExecutorButton = findViewById(R.id.codeExecutorButton);

        codeExecutorButton.setOnClickListener(view -> {
            view.setEnabled(Boolean.FALSE);
            view.setVisibility(View.INVISIBLE);
            Snackbar.make(view, "Executing...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            executingNotificator.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            resultNotificator.setVisibility(View.INVISIBLE);

            disposable = executeCode()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                executingNotificator.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                resultNotificator.setText(result);
                                resultNotificator.setVisibility(View.VISIBLE);
                                view.setEnabled(Boolean.TRUE);
                                view.setVisibility(View.VISIBLE);
                            },
                            Throwable::printStackTrace);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private Single<String> executeCode() {
        // Data for analysis

        // Data Elements
        String opv0Id = "x3Do5e7g4Qo"; // OPV0 doses given
        String opv1Id = "pikOziyCXbM"; // OPV1 doses given
        String opv2Id = "O05mAByOgAv"; // OPV2 doses given
        String opv3Id = "vI2csg55S9C"; // OPV3 doses given

        // Categories
        String fixedOutreah = "fMZEcRHuamy";    // Category "Location Fixed/Outreach"
        String fixed = "qkPbeWaFsnU";           // CategoryOption "Fixed"
        String outreach = "wbrDrL2aYEc";        // CategoryOption "Outreach"

        String age1year = "YNZyaJHiHYq";        // Category "EPI/nutrition age" (< 1 year, > 1 year)
        String lower1year = "btOyqprQ9e8";      // CategoryOption "<1y"
        String greater1year = "GEqzEKCHoGA";      // CategoryOption ">1y"

        // Indicators
        String opv0Percentage = "UWV8MZEfoC4"; // OPV0 %

        // OrganisationUnit
        String ngelehunCHC = "DiszpKrYNg8"; // Ngelehun CHC, or you can use relative "UserOrganisationUnit"



        Result<DimensionalResponse, AnalyticsException> result =
                Sdk.d2().analyticsModule().analytics()
                        .blockingEvaluate();



        return Single.just(AnalyticsHelper.prettyPrint(result));
    }

}
