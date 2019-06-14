package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.main.MainActivity;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TrackedEntityInstanceActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_tracked_entity_instances);
        Toolbar toolbar = findViewById(R.id.tracked_entity_instances_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        observeTrackedEntityInstances();
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
        RecyclerView trackedEntityInstancesRecyclerView = findViewById(R.id.tracked_entity_instance_recycler_view);
        trackedEntityInstancesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TrackedEntityInstanceAdapter adapter = new TrackedEntityInstanceAdapter();
        trackedEntityInstancesRecyclerView.setAdapter(adapter);

        compositeDisposable.add(Observable.fromIterable(Sdk.d2().organisationUnitModule().organisationUnits
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE).get())
                .map(BaseIdentifiableObject::uid)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(organisationUnitUids -> Sdk.d2().trackedEntityModule().trackedEntityInstances
                        .withEnrollments()
                        .getPaged(20))
                .subscribe(trackedEntityInstances ->
                        trackedEntityInstances.observe(this, trackedEntityInstancePagedList -> {
                            adapter.setTrackedEntityInstances(trackedEntityInstancePagedList);
                            findViewById(R.id.tracked_entity_instance_notificator).setVisibility(
                                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
                            })));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
