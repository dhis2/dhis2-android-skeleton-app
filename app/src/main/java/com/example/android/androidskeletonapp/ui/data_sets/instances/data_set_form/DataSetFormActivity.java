package com.example.android.androidskeletonapp.ui.data_sets.instances.data_set_form;

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
import com.example.android.androidskeletonapp.data.service.forms.DataSetFormService;
import com.example.android.androidskeletonapp.data.service.forms.EventFormService;
import com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding;
import com.example.android.androidskeletonapp.ui.enrollment_form.FormAdapter;

import org.hisp.dhis.android.core.datavalue.DataValueObjectRepository;
import org.hisp.dhis.android.core.maintenance.D2Error;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class DataSetFormActivity extends AppCompatActivity {

    private ActivityEnrollmentFormBinding binding;
    private FormAdapter adapter;
    private CompositeDisposable disposable;

    private enum IntentExtra {
        DATASET_UID, OU_UID, PERIOD, ATTR_OPT_COMB
    }

    public static Intent getFormActivityIntent(Context context, String dataSetuid, String periodId,
                                               String orgUnitUid, String attrOptComb) {
        Intent intent = new Intent(context, DataSetFormActivity.class);
        intent.putExtra(IntentExtra.DATASET_UID.name(), dataSetuid);
        intent.putExtra(IntentExtra.PERIOD.name(), periodId);
        intent.putExtra(IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(IntentExtra.ATTR_OPT_COMB.name(), attrOptComb);
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
            DataValueObjectRepository valueRepository = Sdk.d2().dataValueModule().dataValues().value(
                    getIntent().getStringExtra(IntentExtra.PERIOD.name()),
                    getIntent().getStringExtra(IntentExtra.OU_UID.name()),
                    fieldUid.split("_")[0],
                    fieldUid.split("_")[1],
                    getIntent().getStringExtra(IntentExtra.ATTR_OPT_COMB.name())
            );
            try {
                if(!isEmpty(value)){
                    valueRepository.blockingSet(value);
                }else{
                    valueRepository.blockingDeleteIfExist();
                }
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            }
        });
        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.formRecycler.setAdapter(adapter);

        DataSetFormService.getInstance().init(
                Sdk.d2(),
                getIntent().getStringExtra(IntentExtra.DATASET_UID.name()),
                getIntent().getStringExtra(IntentExtra.OU_UID.name()),
                getIntent().getStringExtra(IntentExtra.PERIOD.name()),
                getIntent().getStringExtra(IntentExtra.ATTR_OPT_COMB.name()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        disposable = new CompositeDisposable();

        disposable.add(
                DataSetFormService.getInstance().getDataSetFields()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fieldData -> adapter.updateData(fieldData),
                                Throwable::printStackTrace
                        )
        );
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
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
