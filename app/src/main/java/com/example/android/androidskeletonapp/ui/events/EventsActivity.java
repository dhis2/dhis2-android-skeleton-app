package com.example.android.androidskeletonapp.ui.events;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.event_form.EventFormActivity;

import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EventsActivity extends ListActivity {
    private String selectedProgram;
    private CompositeDisposable compositeDisposable;
    private EventAdapter adapter;
    private final int EVENT_RQ = 1210;

    private enum IntentExtra {
        PROGRAM
    }

    public static Intent getIntent(Context context, String programUid) {
        Bundle bundle = new Bundle();
        if (!isEmpty(programUid))
            bundle.putString(IntentExtra.PROGRAM.name(), programUid);
        Intent intent = new Intent(context, EventsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_events, R.id.eventsToolbar, R.id.eventsRecyclerView);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        compositeDisposable = new CompositeDisposable();
        observeEvents();

        if (isEmpty(selectedProgram))
            findViewById(R.id.eventButton).setVisibility(View.GONE);

        findViewById(R.id.eventButton).setOnClickListener(view ->
                compositeDisposable.add(
                        Sdk.d2().programModule().programs().uid(selectedProgram).get()
                                .map(program -> {
                                    String orgUnit = Sdk.d2().organisationUnitModule().organisationUnits()
                                            .byProgramUids(Collections.singletonList(selectedProgram))
                                            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                            .one().blockingGet().uid();
                                    String stage = Sdk.d2().programModule().programStages()
                                            .byProgramUid().eq(program.uid())
                                            .one().blockingGet().uid();
                                    String attrOptionCombo = program.categoryCombo() != null ?
                                            Sdk.d2().categoryModule().categoryOptionCombos()
                                                    .byCategoryComboUid().eq(program.categoryComboUid())
                                                    .one().blockingGet().uid() : null;
                                    return Sdk.d2().eventModule().events()
                                            .blockingAdd(
                                                    EventCreateProjection.builder()
                                                            .organisationUnit(orgUnit)
                                                            .program(program.uid())
                                                            .programStage(stage)
                                                            .attributeOptionCombo(attrOptionCombo)
                                                            .build()
                                            );
                                })
                                .map(eventUid ->
                                        EventFormActivity.getFormActivityIntent(EventsActivity.this,
                                                eventUid,
                                                selectedProgram,
                                                Sdk.d2().organisationUnitModule().organisationUnits()
                                                        .one().blockingGet().uid(), EventFormActivity.FormType.CREATE))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        activityIntent ->
                                                ActivityStarter.startActivityForResult(
                                                        EventsActivity.this, activityIntent, EVENT_RQ),
                                        Throwable::printStackTrace
                                ))
        );

    }

    private void observeEvents() {
        adapter = new EventAdapter(this);
        recyclerView.setAdapter(adapter);

        getEventRepository().getPaged(20).observe(this, eventsPagedList -> {
            adapter.setSource(eventsPagedList.getDataSource());
            adapter.submitList(eventsPagedList);
            findViewById(R.id.eventsNotificator).setVisibility(
                    eventsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private EventCollectionRepository getEventRepository() {
        EventCollectionRepository eventRepository =
                Sdk.d2().eventModule().events().withTrackedEntityDataValues();
        if (!isEmpty(selectedProgram)) {
            return eventRepository.byProgramUid().eq(selectedProgram);
        } else {
            return eventRepository;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EVENT_RQ && resultCode == RESULT_OK) {
            adapter.invalidateSource();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
