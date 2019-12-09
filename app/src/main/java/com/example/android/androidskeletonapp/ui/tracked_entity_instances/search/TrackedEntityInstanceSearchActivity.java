package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.utils.Exercise;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstanceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.List;

public class TrackedEntityInstanceSearchActivity extends ListActivity {

    private ProgressBar progressBar;
    private TextView downloadDataText;
    private TextView notificator;
    private TrackedEntityInstanceAdapter adapter;

    public static Intent getIntent(Context context) {
        return new Intent(context,TrackedEntityInstanceSearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_tracked_entity_instance_search, R.id.trackedEntityInstancesToolbar,
                R.id.trackedEntityInstanceRecyclerView);

        notificator = findViewById(R.id.dataNotificator);
        downloadDataText = findViewById(R.id.downloadDataText);
        progressBar = findViewById(R.id.trackedEntityInstanceProgressBar);
        FloatingActionButton downloadButton = findViewById(R.id.downloadDataButton);

        adapter = new TrackedEntityInstanceAdapter();

        downloadButton.setOnClickListener(view -> {
            view.setEnabled(Boolean.FALSE);
            view.setVisibility(View.GONE);
            downloadDataText.setVisibility(View.GONE);
            Snackbar.make(view, "Searching data...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notificator.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            searchTrackedEntityInstances();
        });
    }

    private void searchTrackedEntityInstances() {
        recyclerView.setAdapter(adapter);

        getTrackedEntityInstanceQuery().observe(this, trackedEntityInstancePagedList -> {
            adapter.submitList(trackedEntityInstancePagedList);
            downloadDataText.setVisibility(View.GONE);
            notificator.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            findViewById(R.id.searchNotificator).setVisibility(
                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Exercise(
            exerciseNumber = "ex08-teiSearch",
            title = "TrackedEntityInstance query search",
            tips = "Build a trackedEntityInstance query containing:" +
                    "- All organisation units in search scope. You can get the list of root search orgunit " +
                    "(separate call) and use the 'DESCENDANTS' orgunit mode" +
                    "- Program of type 'WITH REGISTRATION' and whose name contains 'malaria' " +
                    "- TEIs with any attribute that contains 'waldo'" +
                    "- Online first method"
    )
    private LiveData<PagedList<TrackedEntityInstance>> getTrackedEntityInstanceQuery() {
        return null;
    }
}
