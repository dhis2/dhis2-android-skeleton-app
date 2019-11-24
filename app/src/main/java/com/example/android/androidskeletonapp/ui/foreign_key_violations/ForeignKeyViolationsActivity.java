package com.example.android.androidskeletonapp.ui.foreign_key_violations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;

public class ForeignKeyViolationsActivity extends ListActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, ForeignKeyViolationsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_fk_violations, R.id.fkViolationsToolbar, R.id.fkViolationsRecyclerView);
        observeFKViolations();
    }

    private void observeFKViolations() {
        ForeignKeyViolationsAdapter adapter = new ForeignKeyViolationsAdapter();
        recyclerView.setAdapter(adapter);

        LiveData<PagedList<ForeignKeyViolation>> liveData = Sdk.d2().maintenanceModule().foreignKeyViolations()
                .getPaged(20);

        liveData.observe(this, fkViolationsPagedList -> {
            adapter.submitList(fkViolationsPagedList);
            findViewById(R.id.fkViolationsNotificator).setVisibility(
                    fkViolationsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
