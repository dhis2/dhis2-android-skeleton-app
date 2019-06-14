package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;

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

import static com.example.android.androidskeletonapp.data.service.LogOutService.logOut;

public class TrackedEntityInstanceActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_tracked_entity_instances);
        Toolbar toolbar = findViewById(R.id.tracked_entity_instances_toolbar);
        setSupportActionBar(toolbar);
        observeTrackedEntityInstances();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_item) {
            compositeDisposable.add(logOut(this));
            return true;
        }

        if (id == R.id.programs_item) {
            ActivityStarter.startActivity(this, ProgramsActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        trackedEntityInstances.observe(this, adapter::setTrackedEntityInstances)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
