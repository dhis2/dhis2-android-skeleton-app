package com.example.android.androidskeletonapp.ui.trackedEntityInstances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;
import static com.example.android.androidskeletonapp.data.service.forms.EnrollmentFormService.saveToEnroll;

public class TrackedEntityInstancesActivity extends ListActivity {

    private CompositeDisposable compositeDisposable;
    private String selectedProgram;

    private String orgUnit;

    private final int ENROLLMENT_RQ = 1210;
    private TrackedEntityInstanceAdapter adapter;

    private enum IntentExtra {
        PROGRAM
    }

    public static Intent getTrackedEntityInstancesActivityIntent(Context context, String program) {
        Intent intent = new Intent(context, TrackedEntityInstancesActivity.class);
        intent.putExtra(IntentExtra.PROGRAM.name(), program);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_tracked_entity_instances, R.id.trackedEntityInstancesToolbar,
                R.id.trackedEntityInstancesRecyclerView);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        compositeDisposable = new CompositeDisposable();
        observeTrackedEntityInstances();
        orgUnit = Sdk.d2().organisationUnitModule().organisationUnits()
                .one().blockingGet().uid();
        if (isEmpty(selectedProgram))
            findViewById(R.id.enrollmentButton).setVisibility(View.GONE);

        findViewById(R.id.enrollmentButton).setOnClickListener(view ->
                compositeDisposable.add(
                        saveToEnroll(selectedProgram, orgUnit, TrackedEntityInstancesActivity.this)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        activityIntent ->
                                                ActivityStarter.startActivityForResult(
                                                        TrackedEntityInstancesActivity.this, activityIntent,ENROLLMENT_RQ),
                                        Throwable::printStackTrace
                                )
                ));
    }
    private void observeTrackedEntityInstances() {
        adapter = new TrackedEntityInstanceAdapter(this, selectedProgram);
        recyclerView.setAdapter(adapter);

        getTeiRepository().getPaged(20).observe(this, trackedEntityInstancePagedList -> {
            adapter.setSource(trackedEntityInstancePagedList.getDataSource());
            adapter.submitList(trackedEntityInstancePagedList);
            findViewById(R.id.trackedEntityInstancesNotificator).setVisibility(
                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private TrackedEntityInstanceCollectionRepository getTeiRepository() {
        TrackedEntityInstanceCollectionRepository teiRepository =
                Sdk.d2().trackedEntityModule().trackedEntityInstances().withTrackedEntityAttributeValues();
        if (!isEmpty(selectedProgram)) {
            List<String> programUids = new ArrayList<>();
            programUids.add(selectedProgram);
            return teiRepository.byProgramUids(programUids);
        } else {
            return teiRepository;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ENROLLMENT_RQ && resultCode == RESULT_OK){
                adapter.invalidateSource();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}
