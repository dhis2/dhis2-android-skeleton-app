package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.enrollment_form.EnrollmentFormActivity;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class TrackedEntityInstancesActivity extends ListActivity {

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
        setUp(R.layout.activity_tracked_entity_instances, R.id.trackedEntityInstancesToolbar,
                R.id.trackedEntityInstancesRecyclerView);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        compositeDisposable = new CompositeDisposable();
        observeTrackedEntityInstances();

        if (isEmpty(selectedProgram))
            findViewById(R.id.enrollmentButton).setVisibility(View.GONE);

        findViewById(R.id.enrollmentButton).setOnClickListener(view -> compositeDisposable.add(
                Sdk.d2().programModule().programs.uid(selectedProgram).getAsync()
                        .map(program -> Sdk.d2().trackedEntityModule().trackedEntityInstances
                                .add(
                                        TrackedEntityInstanceCreateProjection.builder()
                                                .organisationUnit(Sdk.d2().organisationUnitModule().organisationUnits
                                                        .one().get().uid())
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
        ));
    }

    private void observeTrackedEntityInstances() {
        TrackedEntityInstanceAdapter adapter = new TrackedEntityInstanceAdapter();
        recyclerView.setAdapter(adapter);

        getTeiRepository().getPaged(20).observe(this, trackedEntityInstancePagedList -> {
            adapter.submitList(trackedEntityInstancePagedList);
            findViewById(R.id.trackedEntityInstancesNotificator).setVisibility(
                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private TrackedEntityInstanceCollectionRepository getTeiRepository() {
        TrackedEntityInstanceCollectionRepository teiRepository =
                Sdk.d2().trackedEntityModule().trackedEntityInstances.withTrackedEntityAttributeValues();
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
}
