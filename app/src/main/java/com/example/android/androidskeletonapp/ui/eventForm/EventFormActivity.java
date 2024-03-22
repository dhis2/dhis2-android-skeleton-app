package com.example.android.androidskeletonapp.ui.eventForm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.EventFormService;
import com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding;
import com.example.android.androidskeletonapp.ui.enrollmentForm.FormAdapter;

import org.dhis2.form.model.EventMode;
import org.dhis2.form.model.EventRecords;
import org.dhis2.form.ui.FormView;
import org.hisp.dhis.android.core.program.ProgramIndicator;

import java.util.List;

public class EventFormActivity extends AppCompatActivity {

    private FormType formType;
    private String eventUid;
    private String programUid;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid, String programUid,
                                               String orgUnitUid, FormType type) {
        Intent intent = new Intent(context, EventFormActivity.class);
        intent.putExtra(IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(IntentExtra.TYPE.name(), type.name());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEnrollmentFormBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment_form);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        eventUid = getIntent().getStringExtra(IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name());

        formType = FormType.valueOf(getIntent().getStringExtra(IntentExtra.TYPE.name()));

        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.buttonValidate.setOnClickListener(this::evaluateProgramIndicators);


        loadForm();

    }

    private void loadForm() {
        FormView formView = new FormView.Builder()
                .factory(getSupportFragmentManager())
                .setRecords(new EventRecords(eventUid, EventMode.CHECK))
                .useComposeForm(true)
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.formContainer, formView)
                .commit();
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
        setResult(RESULT_OK);
        finish();
    }

    private void evaluateProgramIndicators(View view) {
        List<ProgramIndicator> programIndicators = Sdk.d2().programModule()
                .programIndicators()
                .byProgramUid().eq(programUid)
                .byDisplayInForm().isTrue()
                .blockingGet();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Program indicators");

        if (!programIndicators.isEmpty()) {
            StringBuilder message = new StringBuilder();

            for (ProgramIndicator pi : programIndicators) {
                String value = Sdk.d2().programModule().programIndicatorEngine()
                        .getEventProgramIndicatorValue(eventUid, pi.uid());

                message.append(pi.displayName()).append(": ").append(value).append("\n");
            }

            dialog.setMessage(message);
        } else {
            dialog.setMessage("There are no program indicators for this program");
        }

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (formType == FormType.CREATE)
            EventFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }
}
