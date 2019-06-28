package com.example.android.androidskeletonapp.ui.event_form;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.EventFormService;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.data.service.forms.RuleEngineService;
import com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding;
import com.example.android.androidskeletonapp.ui.enrollment_form.FormAdapter;

import org.apache.commons.lang3.tuple.Pair;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

public class EventFormActivity extends AppCompatActivity {

    private ActivityEnrollmentFormBinding binding;
    private FormAdapter adapter;
    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID
    }

    public static Intent getFormActivityIntent(Context context, String eventUid, String programUid,
                                               String orgUnitUid) {
        Intent intent = new Intent(context, EventFormActivity.class);
        intent.putExtra(IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(IntentExtra.OU_UID.name(), orgUnitUid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment_form);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new FormAdapter((fieldUid, value) -> {
            try {
                Sdk.d2().trackedEntityModule().trackedEntityDataValues.value(
                        EventFormService.getInstance().getEventUid(), fieldUid)
                        .set(value);
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            } finally {
                engineInitialization.onNext(true);
            }
        });
        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.formRecycler.setAdapter(adapter);

        engineInitialization = PublishProcessor.create();

        if (EventFormService.getInstance().init(
                Sdk.d2(),
                getIntent().getStringExtra(IntentExtra.EVENT_UID.name()),
                getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                getIntent().getStringExtra(IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        disposable = new CompositeDisposable();

        disposable.add(
                Flowable.zip(
                        engineService.configure(Sdk.d2(),
                                getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                                EventFormService.getInstance().getEventUid()),
                        EventFormService.getInstance().isListingRendering(),
                        Pair::of
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ruleEngineANDrendering -> {
                                    this.ruleEngine = ruleEngineANDrendering.getLeft();
                                    this.adapter.setListingRendering(ruleEngineANDrendering.getRight());
                                    engineInitialization.onNext(true);
                                },
                                Throwable::printStackTrace
                        )
        );

        disposable.add(
                engineInitialization
                        .flatMap(next ->
                                Flowable.zip(
                                        EventFormService.getInstance().getEventFormFields()
                                                .subscribeOn(Schedulers.io()),
                                        engineService.ruleEvent().flatMap(ruleEvent ->
                                                Flowable.fromCallable(() -> ruleEngine.evaluate(ruleEvent).call()))
                                                .subscribeOn(Schedulers.io()),
                                        this::applyEffects
                                ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fieldData -> adapter.updateData(fieldData),
                                Throwable::printStackTrace
                        )
        );
    }

    private List<FormField> applyEffects(Map<String, FormField> fields,
                                              List<RuleEffect> ruleEffects) {

        for (RuleEffect ruleEffect : ruleEffects) {
            RuleAction ruleAction = ruleEffect.ruleAction();
            if (ruleEffect.ruleAction() instanceof RuleActionHideField) {
                fields.remove(((RuleActionHideField) ruleAction).field());
                for(String key : fields.keySet()) //For image options
                    if(key.contains(((RuleActionHideField) ruleAction).field()))
                        fields.remove(key);
            }

        }

        List<FormField> finalFields = new ArrayList<>(fields.values());

        return finalFields;
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void finishEnrollment(View view) {
        onBackPressed();
    }
}
