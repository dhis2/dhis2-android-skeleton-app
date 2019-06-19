package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.EnrollmentFormService;
import com.example.android.androidskeletonapp.data.service.forms.RuleEngineService;
import com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding;

import org.apache.commons.lang3.tuple.Triple;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
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

public class EnrollmentFormActivity extends AppCompatActivity {

    private ActivityEnrollmentFormBinding binding;
    private FormAdapter adapter;
    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;

    private enum IntentExtra {
        TEI_UID, PROGRAM_UID, OU_UID
    }

    public static Intent getFormActivityIntent(Context context, String teiUid, String programUid,
                                               String orgUnitUid) {
        Intent intent = new Intent(context, EnrollmentFormActivity.class);
        intent.putExtra(IntentExtra.TEI_UID.name(), teiUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(IntentExtra.OU_UID.name(), orgUnitUid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment_form);
        adapter = new FormAdapter();
        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.formRecycler.setAdapter(adapter);

        engineInitialization = PublishProcessor.create();

        if (EnrollmentFormService.getInstance().init(
                Sdk.d2(),
                getIntent().getStringExtra(IntentExtra.TEI_UID.name()),
                getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                getIntent().getStringExtra(IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        disposable = new CompositeDisposable();

        disposable.add(
                engineService.configure(Sdk.d2(),
                        getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                        EnrollmentFormService.getInstance().getEnrollmentUid(),
                        null
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ruleEngine -> {
                                    this.ruleEngine = ruleEngine;
                                    engineInitialization.onNext(true);
                                },
                                Throwable::printStackTrace
                        )
        );

        disposable.add(
                engineInitialization
                        .flatMap(next ->
                                Flowable.zip(
                                        EnrollmentFormService.getInstance().getEnrollmentFormFields(),
                                        engineService.ruleEnrollment().flatMap(ruleEnrollment ->
                                                Flowable.fromCallable(() -> ruleEngine.evaluate(ruleEnrollment).call())),
                                        this::applyEffects
                                ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fieldData -> adapter.updateData(fieldData)
                        )
        );
    }

    private List<Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute,
            TrackedEntityAttributeValueObjectRepository>> applyEffects(Map<String,
            Triple<ProgramTrackedEntityAttribute, TrackedEntityAttribute,
                    TrackedEntityAttributeValueObjectRepository>> fields,
                                                                       List<RuleEffect> ruleEffects) {

        for (RuleEffect ruleEffect : ruleEffects) {
            RuleAction ruleAction = ruleEffect.ruleAction();
            if (ruleEffect.ruleAction() instanceof RuleActionHideField) {
                fields.remove(((RuleActionHideField) ruleAction).field());
            }
        }

        return new ArrayList<>(fields.values());
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.clear();
    }

    private void finishEnrollment(View view) {
        finish();
    }
}
