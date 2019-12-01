package com.example.android.androidskeletonapp.ui.data_sets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.utils.Exercise;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.dataset.DataSet;

public class DataSetsActivity extends ListActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context,DataSetsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_sets, R.id.dataSetsToolbar, R.id.dataSetsRecyclerView);
        observeDataSets();
    }

    private void observeDataSets() {
        DataSetsAdapter adapter = new DataSetsAdapter();
        recyclerView.setAdapter(adapter);

        getDataSetLiveData().observe(this, dataSetPagedList -> {
            adapter.submitList(dataSetPagedList);
            findViewById(R.id.dataSetsNotificator).setVisibility(
                    dataSetPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Exercise(
            exerciseNumber = "ex02",
            version = 1,
            title = "Display the list of all the dataSets",
            tips = "Use the data set repository on the data set module.",
            solutionBranch = "sol02"
    )
    private LiveData<PagedList<DataSet>> getDataSetLiveData() {
        return new MutableLiveData<>();
    }
}
