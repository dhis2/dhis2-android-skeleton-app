package com.example.android.androidskeletonapp.ui.programs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.utils.Exercise;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.events.EventsActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
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

        disposable = getUserOrganisationUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::getProgramsWithOrgUnits)
                .subscribe(programs -> programs.observe(this, programPagedList -> {
                    adapter.submitList(programPagedList);
                    findViewById(R.id.programsNotificator).setVisibility(
                            programPagedList.isEmpty() ? View.VISIBLE : View.GONE);
                }));
    }

    @Exercise(
            exerciseNumber = "ex07a",
            title = "Show a list of the users available programs",
            tips = "Return the full list of organisation units.",
            solutionBranch = "sol07"
    )
    private Single<List<OrganisationUnit>> getUserOrganisationUnits() {
        return Single.just(new ArrayList<>());
    }

    @Exercise(
            exerciseNumber = "ex07b",
            title = "Show a list of the users available programs",
            tips = "Use the UidsHelper to filter the list of programs by organisation unit." +
                    "Order the list by program name." +
                    "Get the the list as a page of 20 items." +
                    "As an extra, filter the list by program type (choose either without or with registration)",
            solutionBranch = "sol07"
    )
    private LiveData<PagedList<Program>> getProgramsWithOrgUnits(List<OrganisationUnit> organisationUnits) {
        return new MutableLiveData<>();
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
                            .getTrackedEntityInstancesActivityIntent(this, programUid),
                    false);
        else
            ActivityStarter.startActivity(this,
                    EventsActivity.getIntent(this,
                            programUid),
                    false);
    }
}
