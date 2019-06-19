package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.main.MainActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TrackedEntityInstancesActivity extends ListActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_tracked_entity_instances, R.id.tracked_entity_instances_toolbar,
                R.id.tracked_entity_instances_recycler_view);
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
        RecyclerView trackedEntityInstancesRecyclerView = findViewById(R.id.tracked_entity_instances_recycler_view);
        trackedEntityInstancesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TrackedEntityInstanceAdapter adapter = new TrackedEntityInstanceAdapter();
        trackedEntityInstancesRecyclerView.setAdapter(adapter);

        disposable = Single.just(Sdk.d2().trackedEntityModule().trackedEntityInstances
                .withTrackedEntityAttributeValues()
                .getPaged(20))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackedEntityInstances -> {
                    trackedEntityInstances.observe(this, trackedEntityInstancePagedList -> {
                        adapter.submitList(trackedEntityInstancePagedList);
                        findViewById(R.id.tracked_entity_instances_notificator).setVisibility(
                                trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
