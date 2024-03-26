package com.example.android.androidskeletonapp.ui.enrollmentForm;

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
import com.example.android.androidskeletonapp.data.service.forms.EnrollmentFormService;

import org.dhis2.form.model.EnrollmentMode;
import org.dhis2.form.model.EnrollmentRecords;
import org.dhis2.form.ui.FormView;

public class EnrollmentFormActivity extends AppCompatActivity {

    private String enrollmentUid;

    private FormType formType;

    private enum IntentExtra {
        TEI_UID, PROGRAM_UID, TYPE
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(
            Context context,
            String teiUid,
            String programUid,
            FormType type
    ) {
        Intent intent = new Intent(context, EnrollmentFormActivity.class);
        intent.putExtra(IntentExtra.TEI_UID.name(), teiUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(IntentExtra.TYPE.name(), type.name());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment_form);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.formRecycler.setVisibility(View.GONE);

        formType = FormType.valueOf(getIntent().getStringExtra(IntentExtra.TYPE.name()));
        String teiUid = getIntent().getStringExtra(IntentExtra.TEI_UID.name());
        String programUid = getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name());
        enrollmentUid = Sdk.d2().enrollmentModule().enrollments()
                .byProgram().eq(programUid)
                .byTrackedEntityInstance().eq(teiUid)
                .one().blockingGet().uid();

        loadForm(enrollmentUid);
    }

    private void loadForm(String enrollmentUid) {
        FormView formView = new FormView.Builder()
                .factory(getSupportFragmentManager())
                .setRecords(new EnrollmentRecords(enrollmentUid, EnrollmentMode.CHECK))
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.formContainer, formView)
                .commit();
    }

    @Override
    protected void onDestroy() {
        EnrollmentFormService.clear();
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

    @Override
    public void onBackPressed() {
        if (formType == FormType.CREATE) {
            EnrollmentFormService.getInstance().delete(enrollmentUid);
        }
        setResult(RESULT_CANCELED);
        finish();
    }
}
