package com.example.android.androidskeletonapp.ui.d2_errors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.maintenance.D2Error;

public class D2ErrorActivity extends ListActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context,D2ErrorActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_d2_errors, R.id.d2ErrorsToolbar, R.id.d2ErrorsRecyclerView);
        observeD2Errors();
    }

    private void observeD2Errors() {
        D2ErrorAdapter adapter = new D2ErrorAdapter();
        recyclerView.setAdapter(adapter);

        LiveData<PagedList<D2Error>> liveData = Sdk.d2().maintenanceModule().d2Errors()
                .getPaged(20);

        liveData.observe(this, d2ErrorPagedList -> {
            adapter.submitList(d2ErrorPagedList);
            findViewById(R.id.d2ErrorsNotificator).setVisibility(
                    d2ErrorPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
