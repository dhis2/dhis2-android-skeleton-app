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

import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetInstance;
import org.jetbrains.annotations.Nullable;

public class DataSetInstancesActivity extends ListActivity {

    private static final String DATASET_UID_EXTRA = "DATASET_UID";

    public static Intent getIntent(Context context) {
        return new Intent(context,DataSetInstancesActivity.class);
    }

    public static Intent getIntent(Context context, String dataSetUid) {
        Bundle extra = new Bundle();
        Intent intent = new Intent(context,DataSetInstancesActivity.class);
        extra.putString(DATASET_UID_EXTRA, dataSetUid);
        intent.putExtras(extra);
        return intent;
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

        LiveData<PagedList<DataSetInstance>> liveData = dataSetsInstances(getDataSet());

        liveData.observe(this, dataSetInstancePagedList -> {
            adapter.submitList(dataSetInstancePagedList);
            findViewById(R.id.dataSetInstancesNotificator).setVisibility(
                    dataSetInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Nullable
    private String getDataSet(){
        return getIntent().getStringExtra(DATASET_UID_EXTRA);
    }

    @Exercise(
            exerciseNumber = "ex05",
            title = "Data set Instances",
            tips = "Apply the dataSetUid filter only if not null. Use getPaged to retrieve results"
    )
    private LiveData<PagedList<DataSetInstance>> dataSetsInstances(
            @Nullable String dataSetUid
    ){
        //TODO
        return null;
    }
}
