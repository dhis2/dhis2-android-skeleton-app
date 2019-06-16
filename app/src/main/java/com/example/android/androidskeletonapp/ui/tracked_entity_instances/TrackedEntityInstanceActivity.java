package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.trackedentity.search.QueryFilter;
import org.hisp.dhis.android.core.trackedentity.search.QueryOperator;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQuery;

import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TrackedEntityInstanceActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;
    private ProgressBar progressBar;
    private TextView downloadDataText;
    private TextView notificator;
    private TrackedEntityInstanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_tracked_entity_instances);
        Toolbar toolbar = findViewById(R.id.tracked_entity_instances_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        notificator = findViewById(R.id.data_notificator);
        downloadDataText = findViewById(R.id.download_data_text);
        TextView teiNotFound = findViewById(R.id.tracked_entity_instance_not_found);
        progressBar = findViewById(R.id.tracked_entity_instance_progress_bar);
        FloatingActionButton downloadButton = findViewById(R.id.download_data_button);

        adapter = new TrackedEntityInstanceAdapter();

        downloadButton.setOnClickListener(view -> {
            view.setEnabled(Boolean.FALSE);
            view.setVisibility(View.GONE);
            downloadDataText.setVisibility(View.GONE);
            teiNotFound.setVisibility(View.GONE);
            Snackbar.make(view, "Downloading data", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notificator.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            syncData();
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

    private void syncData() {
        RecyclerView trackedEntityInstancesRecyclerView = findViewById(R.id.tracked_entity_instance_recycler_view);
        trackedEntityInstancesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trackedEntityInstancesRecyclerView.setAdapter(adapter);

        TrackedEntityInstanceQuery query = TrackedEntityInstanceQuery.builder()
                .orgUnits(Collections.singletonList("DiszpKrYNg8"))
                .orgUnitMode(OuMode.DESCENDANTS)
                .pageSize(15)
                .paging(true)
                .page(1)
                .program("IpHINAT79UW")
                .query(QueryFilter.builder()
                        .filter("a")
                        .operator(QueryOperator.LIKE)
                        .build())
                .build();

        compositeDisposable.add(
                Single.fromCallable(() ->
                Sdk.d2().trackedEntityModule().trackedEntityInstanceQuery
                        .query(query)
                        .onlineFirst().getPaged(15))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackedEntityInstances ->
                        trackedEntityInstances.observe(this, trackedEntityInstancePagedList -> {
                            downloadDataText.setVisibility(View.GONE);
                            notificator.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            adapter.submitList(trackedEntityInstancePagedList);
                            findViewById(R.id.tracked_entity_instance_not_found).setVisibility(
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
