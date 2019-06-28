package com.example.android.androidskeletonapp.ui.programs;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.event_form.EventFormActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramType;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProgramsActivity extends ListActivity implements OnProgramSelectionListener {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_programs, R.id.programsToolbar, R.id.programsRecyclerView);
        observePrograms();
    }

    private void observePrograms() {
        ProgramsAdapter adapter = new ProgramsAdapter(this);
        recyclerView.setAdapter(adapter);

        disposable = Sdk.d2().organisationUnitModule().organisationUnits
                /*.byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)*/.getAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(organisationUnitUids -> Sdk.d2().programModule().programs
                        .byOrganisationUnitList(UidsHelper.getUidsList(organisationUnitUids))
                        .orderByName(RepositoryScope.OrderByDirection.ASC)
                        .withStyle()
                        .withProgramStages()
                        .getPaged(20))
                .subscribe(programs -> programs.observe(this, programPagedList -> {
                    adapter.submitList(programPagedList);
                    findViewById(R.id.programsNotificator).setVisibility(
                            programPagedList.isEmpty() ? View.VISIBLE : View.GONE);
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void onProgramSelected(String programUid, ProgramType programType) {
        if (programType == ProgramType.WITH_REGISTRATION)
            ActivityStarter.startActivity(this,
                    TrackedEntityInstancesActivity
                            .getTrackedEntityInstancesActivityIntent(this, programUid));
        else
            ActivityStarter.startActivity(this,
                    EventFormActivity.getFormActivityIntent(this,
                            null,
                            programUid,
                            Sdk.d2().organisationUnitModule().organisationUnits.one().get().uid()));
    }
}
