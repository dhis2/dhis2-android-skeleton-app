package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.enrollment_form.EnrollmentFormActivity;
import com.example.android.androidskeletonapp.ui.main.MainActivity;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class TrackedEntityInstancesActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;
    private String selectedProgram;

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
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_tracked_entity_instances);
        Toolbar toolbar = findViewById(R.id.tracked_entity_instances_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        observeTrackedEntityInstances();

        if (isEmpty(selectedProgram))
            findViewById(R.id.enrollmentButton).setVisibility(View.GONE);

        findViewById(R.id.enrollmentButton).setOnClickListener(view -> {
            compositeDisposable.add(
                    Single.just(Sdk.d2().programModule().programs.uid(selectedProgram).get())
                            .map(program -> Sdk.d2().trackedEntityModule().trackedEntityInstances
                                    .add(
                                            TrackedEntityInstanceCreateProjection.builder()
                                                    .organisationUnit(Sdk.d2().organisationUnitModule().organisationUnits.one().get().uid())
                                                    .trackedEntityType(program.trackedEntityType().uid())
                                                    .build()
                                    ))
                            .map(teiUid -> EnrollmentFormActivity.getFormActivityIntent(
                                    TrackedEntityInstancesActivity.this,
                                    teiUid,
                                    selectedProgram,
                                    Sdk.d2().organisationUnitModule().organisationUnits.one().get().uid()
                                    ))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    activityIntent ->
                                            ActivityStarter.startActivity(TrackedEntityInstancesActivity.this, activityIntent),
                                    Throwable::printStackTrace
                            )
            );


        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        ActivityStarter.startActivity(this, MainActivity.class);
        return true;
    }

    @Override
    public void onBackPressed() {
        ActivityStarter.startActivity(this, MainActivity.class);
    }

    private void observeTrackedEntityInstances() {
        RecyclerView trackedEntityInstancesRecyclerView = findViewById(R.id.tracked_entity_instances_recycler_view);
        trackedEntityInstancesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TrackedEntityInstanceAdapter adapter = new TrackedEntityInstanceAdapter();
        trackedEntityInstancesRecyclerView.setAdapter(adapter);

        compositeDisposable.add(Single.just(Sdk.d2().trackedEntityModule().trackedEntityInstances
                .withTrackedEntityAttributeValues())
                .map(teiRepository -> {
                    if (!isEmpty(selectedProgram)) {
                        List<String> programUids = new ArrayList<>();
                        programUids.add(selectedProgram);
                        teiRepository = teiRepository.byProgramUids(programUids);
                    }
                    return teiRepository.getPaged(20);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackedEntityInstances -> {
                    trackedEntityInstances.observe(this, trackedEntityInstancePagedList -> {
                        adapter.submitList(trackedEntityInstancePagedList);
                        findViewById(R.id.tracked_entity_instances_notificator).setVisibility(
                                trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
