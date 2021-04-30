package com.example.android.androidskeletonapp.ui.data_sets.instances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.utils.Exercise;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.dataset.DataSetInstance;

public class DataSetInstancesActivity extends ListActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context,DataSetInstancesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_set_instances, R.id.dataSetInstancesToolbar, R.id.dataSetInstancesRecyclerView);
        observeDataSetInstances();
    }

    private void observeDataSetInstances() {
        DataSetInstancesAdapter adapter = new DataSetInstancesAdapter(this);
        recyclerView.setAdapter(adapter);

        LiveData<PagedList<DataSetInstance>> liveData = getDataSetInstancesLiveData();

        liveData.observe(this, dataSetInstancePagedList -> {
            adapter.submitList(dataSetInstancePagedList);
            findViewById(R.id.dataSetInstancesNotificator).setVisibility(
                    dataSetInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Exercise(
            exerciseNumber = "ex04-dataSetInstances",
            title = "Data Set Instances",
            tips = "Filter data set instances by period or period type"
    )
    private LiveData<PagedList<DataSetInstance>> getDataSetInstancesLiveData() {
        return Sdk.d2().dataSetModule().dataSetInstances()
                .getPaged(20);
    }
}
