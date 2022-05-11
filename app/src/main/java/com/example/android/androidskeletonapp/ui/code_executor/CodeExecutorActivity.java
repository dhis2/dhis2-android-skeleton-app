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
import com.example.android.androidskeletonapp.data.utils.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.google.common.collect.Lists;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.hisp.dhis.android.core.category.CategoryOptionCollectionRepository;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.datavalue.DataValueObjectRepository;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.period.Period;

import java.util.List;
import java.util.Objects;

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

    @Exercise(
        exerciseNumber = "ex06a-aggregatedDataCreation",
        title = "Set all the data values of one data set instance",
        tips = "Use the data set module to get the data set Population: aLpVgfXiz0f with its data set elements" +
                "Use the period module to get a period with the same period type that the data set." +
                "Use the organisation unit module to get one organisation unit." +
                "Use the category module to get the attribute option combo (with the " +
                "categoryOptionComboRepository) related to the data set." +
                "Set a random value for each data element (Using nested loops)." +
                "Note that you can get the category option combo from the data element in the loop."
    )
    private Single<String> executeCode() {
        return Single.defer(() -> {
            String DATA_SET_UID = "aLpVgfXiz0f";
            DataSet dataSet = Sdk.d2().dataSetModule().dataSets().withDataSetElements().uid(DATA_SET_UID).blockingGet();
            Single<List<Period>> periods = Sdk.d2().periodModule().periodHelper().getPeriodsForDataSet(DATA_SET_UID);
            Period period = periods.blockingGet().get(0);
            OrganisationUnit organisationUnit = Sdk.d2()
                    .organisationUnitModule()
                    .organisationUnits()
                    .byDataSetUids(Lists.newArrayList(dataSet.uid()))
                    .one().blockingGet();

            CategoryOptionCombo dataSetOptionCombo = Sdk.d2()
                    .categoryModule()
                    .categoryOptionCombos()
                    .byCategoryComboUid()
                    .eq(dataSet.categoryCombo().uid())
                    .one().blockingGet();

            for (DataSetElement dataSetElement : Objects.requireNonNull(dataSet.dataSetElements())) {
                DataElement dataElement = Sdk.d2().dataElementModule().dataElements().uid(dataSetElement.dataElement().uid()).blockingGet();
                String categoryComboUid = dataElement.categoryComboUid();
                List<CategoryOptionCombo> categoryOptionCombos = Sdk.d2()
                        .categoryModule()
                        .categoryOptionCombos()
                        .byCategoryComboUid()
                        .eq(categoryComboUid)
                        .blockingGet();

                for (CategoryOptionCombo optionCombo : categoryOptionCombos) {
                    DataValueObjectRepository dataValueObjectRepository = Sdk.d2()
                            .dataValueModule()
                            .dataValues()
                            .value(period.periodId(), organisationUnit.uid(), dataElement.uid(), optionCombo.uid(), dataSetOptionCombo.uid());

                    try {
                        dataValueObjectRepository.blockingSet(String.valueOf((Math.random() * 20)));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            return Single.just("Execution done!");
        });
    }
}
