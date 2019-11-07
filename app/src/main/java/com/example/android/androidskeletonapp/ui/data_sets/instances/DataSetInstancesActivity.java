package com.example.android.androidskeletonapp.ui.data_sets.instances;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.dataset.DataSetInstance;

public class DataSetInstancesActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_set_instances, R.id.dataSetInstancesToolbar, R.id.dataSetInstancesRecyclerView);
        observeDataSetInstances();
    }

    private void observeDataSetInstances() {
        DataSetInstancesAdapter adapter = new DataSetInstancesAdapter();
        recyclerView.setAdapter(adapter);

        LiveData<PagedList<DataSetInstance>> liveData = Sdk.d2().dataSetModule().dataSetInstances()
                .getPaged(20);

        liveData.observe(this, dataSetInstancePagedList -> {
            adapter.submitList(dataSetInstancePagedList);
            findViewById(R.id.dataSetInstancesNotificator).setVisibility(
                    dataSetInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
