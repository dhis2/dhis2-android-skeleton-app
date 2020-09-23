package com.example.android.androidskeletonapp.ui.programs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.events.EventsActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProgramsActivity extends ListActivity implements OnProgramSelectionListener {

    private Disposable disposable;

    public static Intent getProgramActivityIntent(Context context){
        return new Intent(context,ProgramsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_programs, R.id.programsToolbar, R.id.programsRecyclerView);
        observePrograms();
    }

    private void observePrograms() {
        ProgramsAdapter adapter = new ProgramsAdapter(this);
        recyclerView.setAdapter(adapter);

        disposable = Sdk.d2().organisationUnitModule().organisationUnits().getUids()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::getPrograms)
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

    private LiveData<PagedList<Program>> getPrograms(List<String> organisationUnitUids) {
        return Sdk.d2().programModule().programs()
                .byOrganisationUnitList(organisationUnitUids)
                .orderByName(RepositoryScope.OrderByDirection.ASC)
                .getPaged(20);
    }

    @Override
    public void onProgramSelected(String programUid, ProgramType programType) {
        if (programType == ProgramType.WITH_REGISTRATION)
            ActivityStarter.startActivity(this,
                    TrackedEntityInstancesActivity
                            .getTrackedEntityInstancesActivityIntent(this, programUid),
                    false);
        else
            ActivityStarter.startActivity(this,
                    EventsActivity.getIntent(this,
                            programUid),
                    false);
    }
}
