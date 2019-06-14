package com.example.android.androidskeletonapp.ui.programs;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.main.MainActivity;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ProgramsActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_programs);
        Toolbar toolbar = findViewById(R.id.programs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        observePrograms();
    }

    @Override
    public boolean onSupportNavigateUp() {
        ActivityStarter.startActivity(this, MainActivity.class);
        return true;
    }

    @Override
    public void onBackPressed() {
        ActivityStarter.startActivity(this, MainActivity.class);
    }

    private void observePrograms() {
        RecyclerView programsRecyclerView = findViewById(R.id.programs_recycler_view);
        programsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProgramsAdapter adapter = new ProgramsAdapter();
        programsRecyclerView.setAdapter(adapter);

        compositeDisposable.add(Observable.fromIterable(Sdk.d2().organisationUnitModule().organisationUnits
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
                        adapter.setPrograms(programPagedList);
                        findViewById(R.id.programs_notificator).setVisibility(
                                programPagedList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
