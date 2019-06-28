package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstanceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.search.QueryFilter;
import org.hisp.dhis.android.core.trackedentity.search.QueryOperator;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQuery;

import java.util.Collections;

public class TrackedEntityInstanceSearchActivity extends ListActivity {

    private ProgressBar progressBar;
    private TextView downloadDataText;
    private TextView notificator;
    private TrackedEntityInstanceAdapter adapter;

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
            syncData();
        });
    }

    private void syncData() {
        recyclerView.setAdapter(adapter);

        TrackedEntityInstanceQuery query = TrackedEntityInstanceQuery.builder()
                .orgUnits(Collections.singletonList("YuQRtpLP10I"))
                .orgUnitMode(OrganisationUnitMode.DESCENDANTS)
                .pageSize(15)
                .paging(true)
                .page(1)
                .query(QueryFilter.builder()
                        .filter("a")
                        .operator(QueryOperator.LIKE)
                        .build())
                .build();

        Sdk.d2().trackedEntityModule().trackedEntityInstanceQuery
                .query(query)
                .onlineFirst().getPaged(15).observe(this, trackedEntityInstancePagedList -> {
            adapter.submitList(trackedEntityInstancePagedList);
            downloadDataText.setVisibility(View.GONE);
            notificator.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            findViewById(R.id.searchNotificator).setVisibility(
                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
