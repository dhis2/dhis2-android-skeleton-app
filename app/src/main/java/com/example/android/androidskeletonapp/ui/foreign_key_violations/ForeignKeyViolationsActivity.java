package com.example.android.androidskeletonapp.ui.foreign_key_violations;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

public class ForeignKeyViolationsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_fk_violations, R.id.fkViolationsToolbar, R.id.fkViolationsRecyclerView);
        observeFKViolations();
    }

    private void observeFKViolations() {
        ForeignKeyViolationsAdapter adapter = new ForeignKeyViolationsAdapter();
        recyclerView.setAdapter(adapter);

        LiveData<PagedList<ForeignKeyViolation>> liveData = Sdk.d2().maintenanceModule().foreignKeyViolations
                .getPaged(20);

        liveData.observe(this, fkViolationsPagedList -> {
            adapter.submitList(fkViolationsPagedList);
            findViewById(R.id.fkViolationsNotificator).setVisibility(
                    fkViolationsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
