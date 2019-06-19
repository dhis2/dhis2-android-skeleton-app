package com.example.android.androidskeletonapp.ui.programs;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProgramsActivity extends ListActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_programs, R.id.programs_toolbar, R.id.programs_recycler_view);
        observePrograms();
    }

    private void observePrograms() {
        ProgramsAdapter adapter = new ProgramsAdapter();
        recyclerView.setAdapter(adapter);

        disposable = Observable.fromIterable(Sdk.d2().organisationUnitModule().organisationUnits
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE).get())
                .map(BaseIdentifiableObject::uid)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(organisationUnitUids -> Sdk.d2().programModule().programs
                        .byOrganisationUnitList(organisationUnitUids)
                        .orderByName(RepositoryScope.OrderByDirection.ASC)
                        .withStyle()
                        .withProgramStages()
                        .getPaged(20))
                .subscribe(programs -> {
                    programs.observe(this, programPagedList -> {
                        adapter.submitList(programPagedList);
                        findViewById(R.id.programs_notificator).setVisibility(
                                programPagedList.isEmpty() ? View.VISIBLE : View.GONE);
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
