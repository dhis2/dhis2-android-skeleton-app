package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.EventFormService;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.databinding.ActivityTrackedEntityInstanceSearchBinding;
import com.example.android.androidskeletonapp.ui.base.ListWithoutBindingsActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstanceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TrackedEntityInstanceSearchActivity extends ListWithoutBindingsActivity {

    private ProgressBar progressBar;
    private TextView notificator;
    private TrackedEntityInstanceAdapter adapter;
    private SearchFormAdapter searchFormAdapter;
    private ActivityTrackedEntityInstanceSearchBinding binding;
    private CompositeDisposable disposable;

    private String savedAttribute;
    private String savedProgram;
    private String savedFilter;

    public static Intent getIntent(Context context) {
        return new Intent(context, TrackedEntityInstanceSearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,
                R.layout.activity_tracked_entity_instance_search);
        setUp(R.id.trackedEntityInstanceSearchToolbar, R.id.trackedEntityInstanceRecyclerView);

        disposable = new CompositeDisposable();

        notificator = findViewById(R.id.dataNotificator);
        progressBar = findViewById(R.id.trackedEntityInstanceProgressBar);
        FloatingActionButton downloadButton = findViewById(R.id.downloadDataButton);

        searchFormAdapter = new SearchFormAdapter((fieldUid, value) -> {
            if (fieldUid.equals("Attribute")) {
                savedAttribute = value;
            } else if (fieldUid.equals("Program")) {
                savedProgram = value;
            }
            if (savedAttribute != null && savedProgram != null) {
                downloadButton.setEnabled(true);
            }
        });

        binding.searchFormRecycler.setAdapter(searchFormAdapter);

        adapter = new TrackedEntityInstanceAdapter();

        downloadButton.setOnClickListener(view -> {
            view.setVisibility(View.GONE);
            binding.searchText.setVisibility(View.GONE);
            binding.searchFormRecycler.setVisibility(View.GONE);
            binding.filtersBackground.setVisibility(View.GONE);
            notificator.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            savedFilter = binding.searchText.getText().toString();
            findViewById(R.id.searchNotificator).setVisibility(View.GONE);
            search();
        });

        downloadButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        disposable.add(
                getAttributesFields()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fieldData -> searchFormAdapter.updateData(fieldData),
                                Throwable::printStackTrace
                        )
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.clear();
    }

    @Override
    protected void onDestroy() {
        EventFormService.clear();
        super.onDestroy();
    }

    private Flowable<List<FormField>> getAttributesFields() {
        return Flowable.fromCallable(() -> {
            List<FormField> list = new ArrayList<>();
            list.add(new FormField(null, null, ValueType.TEXT, "Attribute", null, null, true, null));
            list.add(new FormField(null, null, ValueType.TEXT, "Program", null, null, true, null));
            return list;
        });
    }

    private void search() {
        recyclerView.setAdapter(adapter);

        getTrackedEntityInstanceQuery().observe(this, trackedEntityInstancePagedList -> {
            adapter.submitList(trackedEntityInstancePagedList);
            notificator.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            binding.searchText.setVisibility(View.VISIBLE);
            binding.searchFormRecycler.setVisibility(View.VISIBLE);
            binding.filtersBackground.setVisibility(View.VISIBLE);
            findViewById(R.id.downloadDataButton).setVisibility(View.VISIBLE);
            findViewById(R.id.searchNotificator).setVisibility(
                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private LiveData<PagedList<TrackedEntityInstance>> getTrackedEntityInstanceQuery() {
        List<OrganisationUnit> organisationUnits = Sdk.d2().organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .byRootOrganisationUnit(true)
                .blockingGet();

        List<String> organisationUids = new ArrayList<>();
        if (!organisationUnits.isEmpty()) {
            organisationUids = UidsHelper.getUidsList(organisationUnits);
        }

        return Sdk.d2().trackedEntityModule()
                .trackedEntityInstanceQuery()
                .byOrgUnits().in(organisationUids)
                .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
                .byProgram().eq(savedProgram)
                .byFilter(savedAttribute).like(savedFilter)
                .onlineFirst().getPaged(15);
    }
}
